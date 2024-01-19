package ca.ulaval.glo2004.gui.rendering;

import ca.ulaval.glo2004.domaine.Controller;
import ca.ulaval.glo2004.domaine.MeshFactory;
import ca.ulaval.glo2004.domaine.Orientation;
import ca.ulaval.glo2004.domaine.Selectable;
import ca.ulaval.glo2004.domaine.chalet.STLChaletExporter.STLBuilder;
import ca.ulaval.glo2004.domaine.chalet.Wall;
import ca.ulaval.glo2004.domaine.chalet.dto.*;
import ca.ulaval.glo2004.gui.events.EventType;
import ca.ulaval.glo2004.gui.events.Input;
import ca.ulaval.glo2004.gui.events.MouseDragMode;
import ca.ulaval.glo2004.gui.events.Observer;
import ca.ulaval.glo2004.gui.selection.SelectionType;
import ca.ulaval.glo2004.rendering.*;
import ca.ulaval.glo2004.rendering.awt.AWTARGBTexture;
import ca.ulaval.glo2004.rendering.pipeline.ClearStates;
import ca.ulaval.glo2004.rendering.pipeline.RenderStates;
import ca.ulaval.glo2004.rendering.rasterizing.Rasterizer;
import ca.ulaval.glo2004.rendering.renderers.Renderer;
import ca.ulaval.glo2004.rendering.renderers.OverlayRenderer;
import ca.ulaval.glo2004.rendering.shaders.ArrowShader;
import ca.ulaval.glo2004.rendering.shaders.BlurShader;
import ca.ulaval.glo2004.rendering.shaders.StrokeShader;
import ca.ulaval.glo2004.rendering.shaders.UnlitShader;
import ca.ulaval.glo2004.rendering.utils.*;
import ca.ulaval.glo2004.util.math.*;
import ca.ulaval.glo2004.util.math.animation.EaseInEaseOut;
import ca.ulaval.glo2004.util.math.animation.Tween;
//import de.javagl.obj.FloatTuple;
//import de.javagl.obj.Obj;
//import de.javagl.obj.ObjFace;
//import de.javagl.obj.ObjReader;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.*;

public class RenderingCanvas extends Canvas implements Runnable, Observer {

    private boolean isRunning = false;
    private Thread renderingThread;
    private BufferStrategy bufferStrategy;

    private Rasterizer rasterizer;
    private RenderingStats renderingStats;

    private int width;
    private int height;
    private int scaledWidth;
    private int scaledHeight;
    public float RESOLUTION_FACTOR = 1f;
    public static final int TARGET_FPS = 60;

    private float timer = 0;
    private float lastFrameTime = 0;

    private int framecount = 0;
    private int fps = 0;


    private BufferedImageView camera1View;
    private BufferedImageView camera2View;

    private Scene scene;
    private Scene gridScene;
    private Scene overlayScene;
    //    private SceneObject teapotObject;
    private Camera camera1;
    private Camera camera2;

    private Camera hudCamera;

//    private Engine3D renderer;
    private Renderer renderer;
    private OverlayRenderer overlayRenderer;

    private Input input;

    private Controller controller;

    private Orientation currentOrientation;
    private Vec3 destDirection = new Vec3(0, 0, 1);
    private Vec3 currentDirection = new Vec3(0, 0, 1);

    private Quaternion currentObjectRotation = Quaternion.fromAxis(Vec3.up(), (float) Math.toRadians(-90f));
    private Quaternion currentCamRot = Quaternion.identity();
    private Vec3 currentRotationEuler = new Vec3(0, 0, 0);

    private float transitionAlpha = 0;
    private float projectionAlpha = 0;
    private boolean faceViewMode = false;
    private boolean isDoneTransitioning = false;
    private boolean fixedView;
    private Vec3 viewingPoint = new Vec3(0, 0, -20f);
    private Vec3 cameraLookAtPoint = new Vec3(0, 0, 0);
    private Vec3 faceViewLookAtPoint = new Vec3(0, 0, 0);

    private Vec3 startLerpPosition = new Vec3(0, 0, 0);
    private Vec3 startLerpLookAtPoint = new Vec3(0, 0, 0);
    private Quaternion startLerpCamRotation = Quaternion.identity();
    private Quaternion startLerpObjectRotation = Quaternion.identity();
    private Vec3 startFaceViewLookAtPoint = new Vec3(0, 0, 0);
    private float startZoomLevel = 1.0f;

    private Tween tween = new Tween(1f, new EaseInEaseOut());

    private FixedTextureQueue textureList = new FixedTextureQueue(8);

    private int bgColor = 0xFF676767;

    private float zoomLevel = 1f;
    private float targetZoomLevel = 1.0f;
    private float zoomSpeed = 0.1f; // Adjust this value to control zoom speed
    private float accumulatedScrollDelta = 0.0f;

    private Vec2 fixedViewDimensions = new Vec2(10,10);
    private Vec2 startFixedViewDimensions = new Vec2(10,10);

    private Vec3 lastMousePos = new Vec3(0,0, 0);
    private boolean isMouseLeftDragging = false;
    private boolean isMouseRightDragging = false;
    private Vec3 mouseLeftDragStart = new Vec3(0,0,0);
    private Vec3 mouseRightDragStart = new Vec3(0,0,0);

    private Vec3 objectDragStart = new Vec3(0,0,0);

    private Vec3 lastMouseClickPos = new Vec3(0,0,0);
    private boolean prevLeftMouseClicked = false;
    private boolean prevRightMouseClicked = false;
    private boolean triggerLeftClickNextFrame = false;
    private boolean triggerRightClickNextFrame = false;

    private boolean recenteringPosition = false;
    private boolean recenteringRotation = false;

    private boolean prevMouseLeftClick = false;

    private boolean prevViewSwitchButton = false;

    private ArrayList<SceneObject> cubes = new ArrayList<>();

    private Transform selectedTransform = new Transform();
    private SceneObject selectedObject = null;
    private SceneObject lastHoveredObject = null;
    private SceneObject hoveredObject = null;
    private Vec3 hoveredPoint = null;

    private SceneObject testObject = new SceneObject("testobj");

    private java.util.List<RaycastResult> raycastList = new ArrayList<>();
    private java.util.List<RaycastResult> overlayRaycastList = new ArrayList<>();
    private Vec3 lastDragPos = new Vec3(0,0,0);
    private Vec3 lastSizeDrag = new Vec3(1,1,1);
    private Vec3 testObjectSize = new Vec3(1,1,1);

    private boolean prevHasSelectedAccessory = false;
    private Orientation lastHoveredWallOrientation;

    private boolean nextSaveAccPos = false;
    private boolean nextSaveAccDim = false;
    private boolean nextSaveWallDim = false;

    private Point2D<Imperial> accOldPosValue = null;
    private Dimensions<Imperial> accOldDimValue = null;
    private Dimensions<Imperial> wallOldDimValue = null;
    private java.util.List<AccessoryDTO> accCorrespondingOldListValue = new ArrayList<>();
    private java.util.List<AccessoryDTO> accOtherOldListValue = new ArrayList<>();

    private Point2D<Imperial> accSavePosValue = null;
    private Dimensions<Imperial> accSaveDimValue = null;
    private Dimensions<Imperial> wallSaveDimValue = null;

    UUID selectedUUID = null;

    AWTARGBTexture[] faceTextures = new AWTARGBTexture[]{
            AWTARGBTexture.createFromFile("side2.png"),
            AWTARGBTexture.createFromFile("top.png"),
            AWTARGBTexture.createFromFile("back.png"),
            AWTARGBTexture.createFromFile("back.png"),
            AWTARGBTexture.createFromFile("front.png"),
            AWTARGBTexture.createFromFile("side1.png"),
    };

    java.util.List<StringDrawable> stringsToDraw = new ArrayList<>();

    Tween dimAxisTween = new Tween(1f, new EaseInEaseOut());

    Tween roofVisibilityTween = new Tween(1f, new EaseInEaseOut());

    Tween[] wallVisibilities = new Tween[4];

//    private Mesh2 natureMesh = loadModel2("/nature.obj", 0xFFFFFFFF);
//    private Mesh2 beeMesh = loadModel2("/Bee.obj", 0xFFFFFFFF);
//    private Mesh2 challengerMesh = loadModel2("/CHALLENGER71.obj", 0xFFFFFFFF);
//    private Mesh2 fandiskMesh = loadModel2("/fandisk.obj", 0xFFFFFFFF);

    public RenderingCanvas(Controller controller) {
        this.controller = controller;

        this.width = 100;
        this.height = 100;
        this.scaledWidth = (int) (width * RESOLUTION_FACTOR);
        this.scaledHeight = (int) (height * RESOLUTION_FACTOR);

        this.renderingStats = new RenderingStats();
        this.camera1View = new BufferedImageView(this.width, this.height);
        this.camera2View = new BufferedImageView(this.width, this.height);
        this.rasterizer = new Rasterizer(this.renderingStats);
        this.rasterizer.bindView(camera1View);

        this.input = new Input();
        addKeyListener(input);
        addMouseListener(input);
        addMouseMotionListener(input);
        addMouseWheelListener(input);

        this.input.setOnMouseClicked(e -> {
            switch (e.getButton())
            {
                case 1 -> {
                    if (e.getID() == MouseEvent.MOUSE_CLICKED)
                    {
                        if (selectedObject == null)
                        {
                            objectDragStart = getScaledMousePos();
                        }
                        lastMouseClickPos = getScaledMousePos();
                        triggerLeftClickNextFrame = true;
                    }
                    else if (e.getID() == MouseEvent.MOUSE_PRESSED)
                    {


                        if (!this.isMouseLeftDragging)
                            this.mouseLeftDragStart = getScaledMousePos();
                        this.isMouseLeftDragging = true;
                    }

                }
                case 2 -> {
                    if (e.getID() == MouseEvent.MOUSE_CLICKED)
                    {
                        lastMouseClickPos = getScaledMousePos();
                        triggerRightClickNextFrame = true;
                    }
                    else if (e.getID() == MouseEvent.MOUSE_PRESSED)
                    {
                        if (!this.isMouseRightDragging)
                            this.mouseRightDragStart = getScaledMousePos();
                        this.isMouseRightDragging = true;
                    }
                }
            }
        });


        this.input.setOnMouseReleased(e -> {
            switch (e.getButton())
            {
                case 1 -> {
                    this.isMouseLeftDragging = false;
                }
                case 2 -> {
                    this.isMouseRightDragging = false;
                }
            }
        });

        resizeBuffer(100, 100);
    }

//    public Mesh loadModel(String filename, int argb) {
//        java.util.List<Triangle> triangleList = new ArrayList<>();
//
//        InputStream inputStream = getClass().getResourceAsStream(filename);
//        try {
//            assert inputStream != null;
//            Obj obj = ObjReader.read(inputStream);
//
//
//            for (int i = 0; i < obj.getNumFaces(); i++) {
//                ObjFace face = obj.getFace(i);
//
//                assert face.getNumVertices() == 3;
//                FloatTuple v0 = obj.getVertex(face.getVertexIndex(0));
//                FloatTuple v1 = obj.getVertex(face.getVertexIndex(1));
//                FloatTuple v2 = obj.getVertex(face.getVertexIndex(2));
//                triangleList.add(new Triangle(
//                        new Vec3(v0.getX(), v0.getY(), v0.getZ()),
//                        new Vec3(v1.getX(), v1.getY(), v1.getZ()),
//                        new Vec3(v2.getX(), v2.getY(), v2.getZ()),
//                        argb
//                ));
//            }
//        } catch (IOException e) {
//            System.err.println("Could not load the teapot bruh");
//        }
//
//        return new Mesh(triangleList.toArray(new Triangle[0]));
//    }
//
//    public Mesh2 loadModel2(String filename, int argb) {
//        java.util.List<Vertex> vertices = new ArrayList<>();
//        java.util.List<Face> faces = new ArrayList<>();
//
//        InputStream inputStream = getClass().getResourceAsStream(filename);
//        try {
//            assert inputStream != null;
//            Obj obj = ObjReader.read(inputStream);
//
//            for (int i = 0; i < obj.getNumVertices(); i++) {
//                FloatTuple vert = obj.getVertex(i);
//                FloatTuple texcoord = null;
//                try
//                {
//                    texcoord = obj.getTexCoord(i);
//                }
//                catch (Exception ignored)
//                {
//                }
//                Vertex vertex = new Vertex();
//
//                vertex.position = new Vec3(vert.getX(), vert.getY(), vert.getZ());
//                if (texcoord != null)
//                    vertex.uv = new Vec2(texcoord.getX(), texcoord.getY());
//                vertex.color = argb;
//                vertices.add(vertex);
//            }
//
//            for (int i = 0; i < obj.getNumFaces(); i++) {
//                ObjFace face = obj.getFace(i);
//                assert face.getNumVertices() == 3;
////                vertices.get(face.getVertexIndex(0)).color = 0xFFFF0000;
////                vertices.get(face.getVertexIndex(1)).color = 0xFF00FF00;
////                vertices.get(face.getVertexIndex(2)).color = 0xFF0000FF;
//                faces.add(new Face(face.getVertexIndex(0), face.getVertexIndex(1), face.getVertexIndex(2)));
//            }
//        } catch (IOException e) {
//            System.err.println("Could not load the teapot bruh");
//        }
//
//        Mesh2 mesh = new Mesh2(vertices.toArray(new Vertex[0]), faces.toArray(new Face[0]));
//        mesh.material.baseColor = 0xFFFFFFFF;
//        return mesh;
//    }

    public void start() {
        scene = new Scene();

        camera1 = new Camera("Camera1");
        camera2 = new Camera("Camera2");

        hudCamera = new Camera("Hud Camera");

        scene.addObject(camera1);
//        scene.addObject(camera2);

        renderer = new Renderer(scene, camera1);
        renderer.setView(camera1View);

        overlayRenderer = new OverlayRenderer();
        overlayRenderer.setView(camera1View);

        currentOrientation = controller.getOrientation();
        controller.subscribe(EventType.VIEW_CHANGED, this, () -> {
            currentOrientation = controller.getOrientation();
            resetFaceViewMode();
        });
        controller.subscribe(EventType.NEW_PROJECT, this, () -> {
            currentOrientation = controller.getOrientation();
            resetFaceViewMode();
        });

        controller.subscribe(EventType.ZOOM_IN, this, () -> {
            zoomLevel = Math.max(0.05f, zoomLevel - 0.1f);
            recenteringPosition = false;
            calculateCameras();
        });

        controller.subscribe(EventType.ZOOM_OUT, this, () -> {
            zoomLevel += 0.1f;
            recenteringPosition = false;
            calculateCameras();
        });


        for (int i = 0; i < textureList.getData().length; i++) {
            textureList.push(new AWTARGBTexture(this.camera1View.getBufferedImage()));
        }

        for (int i = 0; i < controller.getWallDTOs().length; i++) {
            this.wallVisibilities[i] = new Tween(1,new EaseInEaseOut());
        }


        projectionAlpha = 1;
        transitionAlpha = 1;
        resetFaceViewMode();
        if (!isRunning) {
            isRunning = true;
            renderingThread = new Thread(this);
            renderingThread.start();
        }
    }

    public void stop() {
        if (isRunning) {
            isRunning = false;
            try {
                renderingThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        framerateChecker.start();

        long beginTime;
        long timeDiff = 16;
        int sleepTime;

        int targetMs = (int) ((float) 1 / TARGET_FPS * 1000);
        while (isRunning) {

            beginTime = System.nanoTime() / 1000000;

            float dt = (float) timeDiff /1000;

            update(dt);
            render();

            timeDiff = System.nanoTime() / 1000000 - beginTime;

            sleepTime = targetMs - (int) timeDiff;

//            if (sleepTime > 0) {
//
//                RESOLUTION_FACTOR = MathUtils.clampf(RESOLUTION_FACTOR*1.0625f, 0.125f, 1f);
//                resizeBuffer(width, height);
//            }
//            else
//            {
//                RESOLUTION_FACTOR = MathUtils.clampf(RESOLUTION_FACTOR*(1f - 0.0625f), 0.125f, 1f);
//                resizeBuffer(width, height);
//            }
//            System.out.println(dt);

            timer += dt;
            ++framecount;
        }
    }

    // Timer to check FPS, once per second
    Timer framerateChecker = new Timer(1000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            fps = framecount;
            framecount = 0;
        }
    });

    private void resizeBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        this.scaledWidth = (int) (width * RESOLUTION_FACTOR);
        this.scaledHeight = (int) (height * RESOLUTION_FACTOR);
        this.camera1View.resize(scaledWidth, scaledHeight);
        this.camera2View.resize(scaledWidth, this.height);
    }

    private void update(float dt) {



        if (input.isKeyDown(KeyEvent.VK_1))
        {
            RESOLUTION_FACTOR = 1f;
            resizeBuffer(width, height);
        }
        else if (input.isKeyDown(KeyEvent.VK_2))
        {
            RESOLUTION_FACTOR = 0.5f;
            resizeBuffer(width, height);
        }
        else if (input.isKeyDown(KeyEvent.VK_3))
        {
            RESOLUTION_FACTOR = 0.25f;
            resizeBuffer(width, height);
        }
        else if (input.isKeyDown(KeyEvent.VK_4))
        {
            RESOLUTION_FACTOR = 0.125f;
            resizeBuffer(width, height);
        }

        if (prevMouseLeftClick && !input.isMouseButtonDown(MouseEvent.BUTTON1))
        {
            prevMouseLeftClick = false;
        }

        Vec3 currentMousePos = getScaledMousePos();
        Vec3 currentUnscaledMousePos = getUnscaledMousePos();

        Vec3 mouseDelta = Vec3.sub(currentMousePos, Vec3.mult(lastMousePos, RESOLUTION_FACTOR));//new Vec3(input.getMouseDragDeltaX(), input.getMouseDragDeltaY(), 0);//
        float dragDistance = mouseDelta.magnitude();

        int currWidth = getWidth();
        int currHeight = getHeight();
        if (width != currWidth || height != currHeight) {
            resizeBuffer(currWidth, currHeight);
        }


        calculateCameras();
//        RESOLUTION_FACTOR = 1.0f;//MathUtils.lerp(RESOLUTION_FACTOR, 1f, 0.01f);
//        resizeBuffer(currWidth, currHeight);


        float forwardSpeed = 0;
        float sideSpeed = 0;
        float verticalSpeed = 0;
        float yawSpeed = 0;
        float pitchSpeed = 0;

        if (input.isKeyDown(KeyEvent.VK_W)) {
            forwardSpeed = 1;
        } else if (input.isKeyDown(KeyEvent.VK_S)) {
            forwardSpeed = -1;
        }
        if (input.isKeyDown(KeyEvent.VK_A)) {
            sideSpeed = -1;
        } else if (input.isKeyDown(KeyEvent.VK_D)) {
            sideSpeed = 1;
        }
        if (input.isKeyDown(KeyEvent.VK_SPACE)) {
            verticalSpeed = 1;
        } else if (input.isKeyDown(KeyEvent.VK_CONTROL)) {
            verticalSpeed = -1;
        }

        if (input.getScrollDeltaY() != 0) {
            float zoomDt = (float) input.getScrollDeltaX() / 100f;
            float reducedDt = adjustZoomIncrement(zoomDt, zoomLevel);
            accumulatedScrollDelta += reducedDt;
        }

        float smoothness = 0.05f;
        if (accumulatedScrollDelta != 0) {

            float sign = Math.signum(accumulatedScrollDelta);
            float delta = Math.min(Math.abs(accumulatedScrollDelta), smoothness) * sign;

            if (zoomLevel + delta > 0.05f) {
                zoomLevel += delta;
                zoomFaceView(1 - delta);
            }
            else
            {
                accumulatedScrollDelta = 0;
            }

            accumulatedScrollDelta -= delta;
            calculateCameras();
        }


//        if (input.getMouseDragMode() == MouseDragMode.PANNING)
//        {
            if (input.getMouseDragMode() == MouseDragMode.ORBITTING) {
                pitchSpeed = mouseDelta.y;
                yawSpeed = mouseDelta.x;
            }
            else if (input.getMouseDragMode() == MouseDragMode.PANNING)
            {

                Vec3 a = camera1.screenPointToPerspectivePlaneIntersection(new Vec3(0,0,0));
                Vec3 b = camera1.screenPointToPerspectivePlaneIntersection(mouseDelta);

                Vec3 mouseWorldDelta = Vec3.sub(a, b);

//                Vec3 delta = camera1.worldDragCoordinates(new Vec2(0,0), mouseDelta.toVec2());
//
//
//                float dist = plane.raycast(ray).orElse(0.0f);


//                Vec3 projectedDrag = Vec3.projectOntoPlane(delta, camera1.transform.getForward());
                Vec3 rotatedPt = mouseWorldDelta;//ray.getPoint(dist);//Vec3.rotate(new Vec3(-delta.x, -delta.y, 0), Vec3.mult(camera1.transform.rotation.toEuler(), -1));
                cameraLookAtPoint.add(rotatedPt);
                faceViewLookAtPoint.add(rotatedPt);

                if (mouseDelta.x != 0 || mouseDelta.y != 0)
                {
                    recenteringPosition = false;
                }

            }
//        }


        if (input.isKeyDown(KeyEvent.VK_R)) {
//            this.controller.changeOrientation();
            resetFaceViewMode();
        }

        if (input.isKeyDown(KeyEvent.VK_T) && !prevViewSwitchButton)
        {
            controller.changeOrientation(Orientation.values()[MathUtils.wrap(controller.getOrientation().ordinal()+1, (Orientation.values().length))]);
            prevViewSwitchButton = true;
        }
        else if (!input.isKeyDown(KeyEvent.VK_T))
        {
            prevViewSwitchButton = false;
        }


        if (this.faceViewMode) {
            float cameraDot = Vec3.dot(camera1.transform.getForward(), Vec3.forward);

            if (pitchSpeed != 0 || yawSpeed != 0) {
                this.faceViewMode = false;
            }

            this.transitionAlpha = tween.update(dt);


            float easedLerpFactor = this.transitionAlpha;

            camera1.transform.position.set(Vec3.lerp(startLerpPosition, viewingPoint, easedLerpFactor));
            camera1.transform.rotation.set(startLerpCamRotation.slerp(Quaternion.fromEuler(new Vec3(0, 0, 0)), easedLerpFactor));
            cameraLookAtPoint.set(Vec3.lerp(startLerpLookAtPoint, faceViewLookAtPoint, easedLerpFactor));
            fixedViewDimensions.set(Vec2.lerp(startFixedViewDimensions, getViewDimensionsFromViewOrientation(controller.getOrientation()), easedLerpFactor));

            if (recenteringPosition)
            {
                faceViewLookAtPoint.set(Vec3.lerp(startFaceViewLookAtPoint, new Vec3(0, 0, 0), easedLerpFactor));

                Vec2 viewDim = getViewDimensionsFromViewOrientation(controller.getOrientation());
                float targetZoomLevel = ((getOrthoSizeForDimensions(viewDim.x, viewDim.y, camera1View.getWidth(), camera1View.getHeight()))+5f)/10f;
                zoomLevel = MathUtils.lerp(startZoomLevel, targetZoomLevel, easedLerpFactor);
            }

            projectionAlpha = MathUtils.lerp((cameraDot * cameraDot), projectionAlpha, 0.5f);
        } else {

            camera1.transform.rotate(new Vec3(pitchSpeed, yawSpeed, 0), 0.01f);


//            Vec3 rotatedPt = Vec3.rotate(new Vec3(sideSpeed / 10, 0, forwardSpeed / 10), camera1.transform.rotation.toEuler());
//            cameraLookAtPoint.add(new Vec3(rotatedPt.x, verticalSpeed / 10, rotatedPt.z));

            float cameraDot = Vec3.dot(camera1.transform.getForward(), Vec3.forward);
            projectionAlpha = Math.min((cameraDot*cameraDot), projectionAlpha);
        }

        projectionAlpha = MathUtils.clampf(projectionAlpha, 0, 1);

        bgColor = ColorUtils.lerp(0xFF888888, 0xFF2d2d2d, projectionAlpha);
//        RESOLUTION_FACTOR = MathUtils.lerp(0.5f, 1, transitionAlpha);

        WallDTO[] wallDTOs = controller.getWallDTOs();
        float centerX = wallDTOs[1].width.getRawInchValueFloat() / 2f;
        float centerY = wallDTOs[1].height.getRawInchValueFloat() / 2f + controller.getExtensionDTO().getHeight().getRawInchValueFloat() / 2f;
        float centerZ = wallDTOs[2].width.getRawInchValueFloat() / 2f;
        Vec3 center = new Vec3(centerX / 10f, centerY / 10f, centerZ / 10f);

        int[] orientationNormal = currentOrientation.getNormal();
        Vec3 orientationVec = new Vec3(-orientationNormal[0], -orientationNormal[1], -orientationNormal[2]);

        destDirection = orientationVec;


        Vec3 facingDirection = Vec3.mult(orientationVec, 1);
        Vec3 cameraFacingDirection = Vec3.mult(orientationVec, -1);


        // Calculate the target quaternion based on the forward direction
        Quaternion targetRotation = getSceneRotationFromOrientation(currentOrientation);//Quaternion.fromEuler(Transform.getRotationFromForward(facingDirection));
        Quaternion targetCamRot = Quaternion.fromEuler(Transform.getRotationFromForward(cameraFacingDirection));


        Vec3 lookAtDirection = camera1.transform.getForward();//currentRotation.transform(Vec3.forward).normalized();
        float distanceToCenter = -viewingPoint.z + 15f*zoomLevel;
        // Update the camera's position
        camera1.transform.position = Vec3.add(cameraLookAtPoint, Vec3.mult(lookAtDirection, -distanceToCenter));//ray.getPoint();

        // Set the camera's rotation directly using the calculated quaternion
//        camera1.transform.rotation = currentRotation.normalize();

        currentObjectRotation.set(startLerpObjectRotation.slerp(targetRotation, transitionAlpha));
//        Quaternion lookAtQ = Quaternion.lookAt3(camera1.transform.position, cameraLookAtPoint, Vec3.up);
//        currentCamRot = lookAtQ;//currentCamRot.slerp(targetCamRot, 0.1f);

        // Assuming you have a method like "lookAt" in your Transform class, you can use it to make the camera face the target
//        camera1.transform.rotation = lookAtQ;




        camera2.transform.position = new Vec3((float) 5, (float) 30, 0);
        camera2.transform.rotation = Quaternion.fromEuler(new Vec3((float) Math.toRadians(90), (float) Math.toRadians(0), 0.0f));


        this.scene = new Scene();
        this.gridScene = new Scene();
        this.overlayScene = new Scene();

        scene.addObject(camera1);

        Transform normalizedTransformGroup = new Transform();
        Transform transformGroup = new Transform();

        normalizedTransformGroup.addChild(transformGroup);

        Mesh2 totalMesh = new Mesh2();

        try {
            if(controller.getSelectedObject().isPresent())
                selectedUUID = controller.getSelectedObject().get().getUUID();
        }
        catch (Exception ignored)
        {
        }

        Mesh2 selectedMesh = null;
        Orientation hoveredWallOrientation = controller.getSelectedAccessoryWallOrientation().orElse(null);
        Transform selectedAccTransform = null;
        Vec2 selectedAccDimensions = new Vec2(1,1);
        SceneObject hoveredObject = raycastList.isEmpty() ? null : raycastList.get(0).target;
        boolean isHoveringSelectedObject = false;
        boolean isSelectedObjectValid = false;

        java.util.List<SceneObject> filteredList = new ArrayList<>();
        for (RaycastResult raycastResult : raycastList) {
            if (raycastResult.target.equals(selectedObject) || raycastResult.target.equals(testObject))
            {
                isHoveringSelectedObject = true;
                continue;
            }
            filteredList.add(raycastResult.target);
        }

        for (int i = 0; i < wallDTOs.length; i++) {
            WallDTO wallDTO = wallDTOs[i];
            Mesh2 wallMesh = new Mesh2();

            try {
                wallMesh = MeshFactory.createCutoutMesh(wallDTO, wallDTO.imprecision);
            }
            catch (Exception ignored) {
                System.err.println("Something wrong happened");
            }

            float wallVisib = 1f;
            if (!controller.isShowAllPanels() && (wallDTO.wallOrientation != currentOrientation && currentOrientation != Orientation.TOP))
            {
                wallVisib = 1f - wallVisibilities[i].update(dt);
            }
            else
            {
                wallVisib = 1f - wallVisibilities[i].update(-dt);
            }

            if (wallVisib < 0.01f)
                continue;

            wallMesh.material.baseColor = getWallColor(wallDTO);
            if (wallVisib < 0.99f)
            {
                wallMesh.material.getRenderStates().blend = true;
                wallMesh.material.setRenderPass(RenderPass.TRANSPARENT);
                wallMesh.material.baseColor = ColorUtils.setAlpha(wallMesh.material.baseColor, (int) (255f * wallVisib));
            }

            float totalToRemove = wallDTO.thickness.divide(2).plus(wallDTO.imprecision.divide(2)).getRawInchValueFloat();
            int[] normal = wallDTO.wallOrientation.getNormal();
            Vec3 normalVec = new Vec3(-normal[0], -normal[1], -normal[2]);
            float x = wallDTO.x.getRawInchValueFloat() + (wallDTO.isOvertaking ? 0 : -normal[2] * totalToRemove);
            float y = wallDTO.y.getRawInchValueFloat();
            float z = wallDTO.z.getRawInchValueFloat() + (wallDTO.isOvertaking ? 0 : normal[0] * totalToRemove);
            x /= 10f;
            y /= 10f;
            z /= 10f;

//            totalMesh.add(wallMesh);
//            BlurShader wallShader = new BlurShader();
//            wallShader.sampler = this.camera1View;
//            wallMesh.material.shader = wallShader;
            SceneObject wallObj = new SceneObject(wallDTO.wallOrientation.name(), wallMesh);
            wallObj.setUserData(wallDTO.uuid);

            transformGroup.addChild(wallObj.transform);
            wallObj.transform.position.set(x, y, z);
            wallObj.transform.rotation.set(Quaternion.fromDirection(normalVec));
            wallObj.transform.setScale(0.1f);

            if (selectedUUID == wallDTO.uuid)
            {
                selectObject(wallObj);
                wallObj.mesh_ = new Mesh2();
                selectedMesh = wallMesh;
                selectedAccTransform = wallObj.transform;
                selectedAccDimensions.set(wallDTO.width.getRawInchValueFloat()/10f, wallDTO.height.getRawInchValueFloat()/10f);
            }

            boolean isCurrWallHovered = !filteredList.isEmpty() && filteredList.get(0).descriptor.equals(wallObj.descriptor);
            if (isCurrWallHovered)
            {
                hoveredWallOrientation = wallDTO.wallOrientation;
            }

            scene.addObject(wallObj);

            for (int j = 0; j < wallDTO.accessories.size(); j++) {
                AccessoryDTO accessoryDTO = wallDTO.accessories.get(j);

                float accessoryThickness = wallDTO.thickness.getRawInchValueFloat();
                Mesh2 accessoryMesh = new Mesh2();
                try {
                    accessoryMesh = MeshFactory.createMesh(accessoryDTO, accessoryThickness);
                }
                catch (Exception ignored) {
                    System.err.println("Something wrong happened");
                }

                float accessoryZ = 0;

                if (accessoryDTO.isValid)
                {
                    accessoryZ = accessoryThickness;
                    BlurShader windowBlurShader = new BlurShader();
                    accessoryMesh.material.shader = windowBlurShader;
                    accessoryMesh.material.baseColor = ColorUtils.setAlpha(ColorUtils.multRGB(accessoryMesh.material.baseColor, 0.5f), 80);
                    accessoryMesh.material.setRenderPass(RenderPass.TRANSPARENT);
                    RenderStates rs = accessoryMesh.material.getRenderStates();
                    rs.blend = true;
                    accessoryMesh.material.setOnApplyMaterial(self -> {

                        textureList.push(new AWTARGBTexture(this.camera1View.getBufferedImage()));

                        ((BlurShader)self.shader).samplers = textureList.getData();//new AWTTexture(this.camera1View.getBufferedImage());

                    });
                }
                else
                {
                    accessoryMesh.material.baseColor = ColorUtils.setAlpha(accessoryMesh.material.baseColor, 127);
                    accessoryMesh.material.setRenderPass(RenderPass.TRANSPARENT);
                    RenderStates rs = accessoryMesh.material.getRenderStates();
                    rs.blend = true;
                }


//                totalMesh.add(accessoryMesh);
                SceneObject accessoryObj = new SceneObject(accessoryDTO.name, accessoryMesh);
                accessoryObj.setUserData(accessoryDTO.uuid);
                wallObj.transform.addChild(accessoryObj.transform);

                accessoryObj.transform.setScale(new Vec3(1, 1, 1));

                float overtakingOffest = -(wallDTO.isOvertaking ? 0 : totalToRemove);
                Vec3 relativePosition = new Vec3(accessoryDTO.x.getRawInchValueFloat() + overtakingOffest, accessoryDTO.y.getRawInchValueFloat(), accessoryZ);
//                if (isObjectSelected(accessoryObj.descriptor))
//                {
//                    isHoveringSelectedObject = true;
//                }

                if (wallVisib < 0.99f)
                {
                    accessoryMesh.material.getRenderStates().blend = true;
                    accessoryMesh.material.setRenderPass(RenderPass.TRANSPARENT);
                    accessoryMesh.material.baseColor = ColorUtils.setAlpha(accessoryMesh.material.baseColor, (int) (255f * wallVisib));
                }

                if (selectedUUID == accessoryDTO.uuid)
                {

                    selectedMesh = accessoryMesh;
                    selectedAccTransform = accessoryObj.transform;
                    selectedAccDimensions.set(accessoryDTO.width.getRawInchValueFloat()/10f, accessoryDTO.height.getRawInchValueFloat()/10f);
                    selectObject(accessoryObj);


//                    accessoryObj.transform.set(selectedTransform);
//                    if (selectedObject == null)
//                    {
                        accessoryObj.transform.translate(relativePosition, Space.SELF);
//                        selectedTransform = accessoryObj.transform;
//                        selectedObject = accessoryObj;
//                    }
//                    else
//                    {
//                        accessoryObj.transform.set(selectedTransform);
//                    }

                    isSelectedObjectValid = accessoryDTO.isValid;

                }
                else
                {
                    accessoryObj.transform.translate(relativePosition, Space.SELF);
                    selectedTransform = new Transform();
                    selectedObject = null;
//                    selectedAccTransform = null;
                    scene.addObject(accessoryObj);
                }

            }

        }

        float roofVisibFactor = 1f;
        float roofVisibThreshold = 0.025f;
        if (currentOrientation == Orientation.TOP || !controller.isShowAllPanels())
        {
            roofVisibFactor = 1 - roofVisibilityTween.update(dt);//Vec3.dot(camera1.transform.getForward(), currentObjectRotation.transform(Vec3.forward(-1)));
        }
        else
        {
            roofVisibFactor = 1 - roofVisibilityTween.update(-dt);
        }


        RoofDTO roofDTO = controller.getRoofDTO();
        ExtensionDTO extensionDTO = controller.getExtensionDTO();
        GableDTO leftGableDTO = controller.getLeftGableDTO();
        GableDTO rightGableDTO = controller.getRightGableDTO();
        SlopeDTO slopeDTO = controller.getSlopeDTO();

        // extension
        {
            WallDTO leftWallToExtension = controller.getWallDTOFromOrientation(Orientation.FRONT);
            WallDTO extensionWall = controller.getWallDTOFromOrientation(Orientation.RIGHT);
            switch (roofDTO.orientation) {
                case LEFT:
                    leftWallToExtension = controller.getWallDTOFromOrientation(Orientation.FRONT);
                    extensionWall = controller.getWallDTOFromOrientation(Orientation.RIGHT);
                    break;
                case BACK:
                    leftWallToExtension = controller.getWallDTOFromOrientation(Orientation.LEFT);
                    extensionWall = controller.getWallDTOFromOrientation(Orientation.FRONT);
                    break;
                case RIGHT:
                    leftWallToExtension = controller.getWallDTOFromOrientation(Orientation.BACK);
                    extensionWall = controller.getWallDTOFromOrientation(Orientation.LEFT);
                    break;
                case FRONT:
                    leftWallToExtension = controller.getWallDTOFromOrientation(Orientation.RIGHT);
                    extensionWall = controller.getWallDTOFromOrientation(Orientation.BACK);
                    break;
            }

            Mesh2 extensionMesh = new Mesh2();

            try {
                extensionMesh = MeshFactory.createExtensionMesh(leftWallToExtension, extensionWall, extensionDTO);
            }
            catch (Exception ignored) {
                System.err.println("Extension mesh could not be created");
            }


            extensionMesh.material.baseColor = getWallColor(extensionWall);


            SceneObject extensionObj = new SceneObject(extensionDTO.getUuid().toString(), extensionMesh);
            extensionObj.setUserData(extensionDTO.getUuid());
            extensionObj.transform.setScale(0.1f);

            if (roofVisibFactor < 1f - roofVisibThreshold)
            {
                extensionMesh.material.getRenderStates().blend = true;
                extensionMesh.material.setRenderPass(RenderPass.TRANSPARENT);
                extensionMesh.material.baseColor = ColorUtils.setAlpha(extensionMesh.material.baseColor, (int) (255f * roofVisibFactor));
            }
            else
            {
                if (selectedUUID == extensionDTO.getUuid())
                {
                    selectObject(extensionObj);
                }
            }

            Vec3 position = new Vec3(extensionDTO.getX().getRawInchValueFloat() / 10f, extensionDTO.getY().getRawInchValueFloat() / 10f, extensionDTO.getZ().getRawInchValueFloat() / 10f);
            extensionObj.transform.translate(position, Space.SELF);

            int[] normal = extensionWall.wallOrientation.getNormal();
            Vec3 normalVec = new Vec3(-normal[0], -normal[1], -normal[2]);
            extensionObj.transform.rotation.set(Quaternion.fromDirection(normalVec));

            transformGroup.addChild(extensionObj.transform);

            if (roofVisibFactor > roofVisibThreshold)
            {
                scene.addObject(extensionObj);
            }
        }

        // left gable
        {
            WallDTO leftGableWall = controller.getWallDTOFromOrientation(Orientation.BACK);
            switch (roofDTO.orientation) {
                case LEFT:
                    leftGableWall = controller.getWallDTOFromOrientation(Orientation.BACK);
                    break;
                case BACK:
                    leftGableWall = controller.getWallDTOFromOrientation(Orientation.RIGHT);
                    break;
                case RIGHT:
                    leftGableWall = controller.getWallDTOFromOrientation(Orientation.FRONT);
                    break;
                case FRONT:
                    leftGableWall = controller.getWallDTOFromOrientation(Orientation.LEFT);
                    break;
            }

            Mesh2 leftGableMesh = new Mesh2();
            try {
                leftGableMesh = MeshFactory.createLeftGableMesh(leftGableWall, leftGableDTO);
            }
            catch (Exception ignored) {
                System.err.println("Left gable mesh could not be created");
            }



            SceneObject leftGableObj = new SceneObject(leftGableDTO.getUuid().toString(), leftGableMesh);
            leftGableObj.setUserData(leftGableDTO.getUuid());

            leftGableMesh.material.baseColor = getWallColor(leftGableWall);
            if (roofVisibFactor < 1f - roofVisibThreshold)
            {
                leftGableMesh.material.getRenderStates().blend = true;
                leftGableMesh.material.setRenderPass(RenderPass.TRANSPARENT);
                leftGableMesh.material.baseColor = ColorUtils.setAlpha(leftGableMesh.material.baseColor, (int) (255f * roofVisibFactor));
            }
            else
            {
                if (selectedUUID == leftGableDTO.getUuid())
                {
                    selectObject(leftGableObj);
                }
            }

            leftGableObj.transform.setScale(0.1f);

            Vec3 position = new Vec3(leftGableDTO.getX().getRawInchValueFloat() / 10f, leftGableDTO.getY().getRawInchValueFloat() / 10f, leftGableDTO.getZ().getRawInchValueFloat() / 10f);
            leftGableObj.transform.translate(position, Space.SELF);

            int[] normal = leftGableWall.wallOrientation.getNormal();
            Vec3 normalVec = new Vec3(-normal[0], -normal[1], -normal[2]);
            leftGableObj.transform.rotation.set(Quaternion.fromDirection(normalVec));
            transformGroup.addChild(leftGableObj.transform);

            if (roofVisibFactor > roofVisibThreshold)
            {
                scene.addObject(leftGableObj);
            }
        }

        // right gable
        {
            WallDTO rightGableWall = controller.getWallDTOFromOrientation(Orientation.FRONT);
            switch (roofDTO.orientation) {
                case LEFT:
                    rightGableWall = controller.getWallDTOFromOrientation(Orientation.FRONT);
                    break;
                case BACK:
                    rightGableWall = controller.getWallDTOFromOrientation(Orientation.LEFT);
                    break;
                case RIGHT:
                    rightGableWall = controller.getWallDTOFromOrientation(Orientation.BACK);
                    break;
                case FRONT:
                    rightGableWall = controller.getWallDTOFromOrientation(Orientation.RIGHT);
                    break;
            }

            Mesh2 rightGableMesh = new Mesh2();
            try {
                rightGableMesh = MeshFactory.createRightGableMesh(rightGableWall, leftGableDTO);
            }
            catch (Exception ignored) {
                System.err.println("Left gable mesh could not be created");
            }
            rightGableMesh.material.baseColor = getWallColor(rightGableWall);



            SceneObject rightGableObj = new SceneObject(rightGableDTO.getUuid().toString(), rightGableMesh);
            rightGableObj.setUserData(rightGableDTO.getUuid());

            rightGableObj.transform.setScale(0.1f);

            if (roofVisibFactor < 1 - roofVisibThreshold)
            {
                rightGableMesh.material.getRenderStates().blend = true;
                rightGableMesh.material.setRenderPass(RenderPass.TRANSPARENT);
                rightGableMesh.material.baseColor = ColorUtils.setAlpha(rightGableMesh.material.baseColor, (int) (255f * roofVisibFactor));
            }
            else
            {
                if (selectedUUID == rightGableDTO.getUuid())
                {
                    selectObject(rightGableObj);
                }
            }

            Vec3 position = new Vec3(rightGableDTO.getX().getRawInchValueFloat() / 10f, rightGableDTO.getY().getRawInchValueFloat() / 10f, rightGableDTO.getZ().getRawInchValueFloat() / 10f);
            position.add(new Vec3(0, 0, -rightGableWall.thickness.getRawInchValueFloat()/10f));
            rightGableObj.transform.translate(position, Space.SELF);

            int[] normal = rightGableWall.wallOrientation.getNormal();
            Vec3 normalVec = new Vec3(-normal[0], -normal[1], -normal[2]);
            rightGableObj.transform.rotation.set(Quaternion.mult(Quaternion.fromDirection(normalVec), Quaternion.fromEuler(new Vec3(0,(float)Math.toRadians(180),0))));
            transformGroup.addChild(rightGableObj.transform);

            if (roofVisibFactor > roofVisibThreshold)
            {
                scene.addObject(rightGableObj);
            }
        }

        // slope
        {
            WallDTO frontSlopeWall = controller.getWallDTOFromOrientation(Orientation.LEFT);
            WallDTO leftOfSlopeWall = controller.getWallDTOFromOrientation(Orientation.BACK);
            switch (roofDTO.orientation) {
                case LEFT:
                    frontSlopeWall = controller.getWallDTOFromOrientation(Orientation.LEFT);
                    leftOfSlopeWall = controller.getWallDTOFromOrientation(Orientation.BACK);
                    break;
                case BACK:
                    frontSlopeWall = controller.getWallDTOFromOrientation(Orientation.BACK);
                    leftOfSlopeWall = controller.getWallDTOFromOrientation(Orientation.RIGHT);
                    break;
                case RIGHT:
                    frontSlopeWall = controller.getWallDTOFromOrientation(Orientation.RIGHT);
                    leftOfSlopeWall = controller.getWallDTOFromOrientation(Orientation.FRONT);
                    break;
                case FRONT:
                    frontSlopeWall = controller.getWallDTOFromOrientation(Orientation.FRONT);
                    leftOfSlopeWall = controller.getWallDTOFromOrientation(Orientation.LEFT);
                    break;
            }

            Mesh2 slopeMesh = new Mesh2();
            try {
                slopeMesh = MeshFactory.createSlopeMesh(frontSlopeWall, leftOfSlopeWall, slopeDTO);
            }
            catch (Exception e) {
                System.err.println("Slope mesh could not be created");
            }

            slopeMesh.material.baseColor = getWallColor(frontSlopeWall);


            SceneObject slopeObj = new SceneObject(slopeDTO.getUuid().toString(), slopeMesh);
            slopeObj.setUserData(slopeDTO.getUuid());

            slopeObj.transform.setScale(0.1f);

            if (roofVisibFactor < 1f - roofVisibThreshold)
            {
                slopeMesh.material.getRenderStates().blend = true;
                slopeMesh.material.setRenderPass(RenderPass.TRANSPARENT);
                slopeMesh.material.baseColor = ColorUtils.setAlpha(slopeMesh.material.baseColor, (int) (255f * roofVisibFactor));
            }
            else
            {
                if (selectedUUID == slopeDTO.getUuid())
                {
                    selectObject(slopeObj);
                }
            }

            Vec3 position = new Vec3(slopeDTO.getX().getRawInchValueFloat() / 10f, slopeDTO.getY().getRawInchValueFloat() / 10f, slopeDTO.getZ().getRawInchValueFloat() / 10f);
            slopeObj.transform.translate(position, Space.SELF);
            int[] normal = frontSlopeWall.wallOrientation.getNormal();
            Vec3 normalVec = new Vec3(-normal[0], -normal[1], -normal[2]);
            slopeObj.transform.rotation.set(Quaternion.fromDirection(normalVec));
            transformGroup.addChild(slopeObj.transform);

            if (roofVisibFactor > roofVisibThreshold)
            {
                scene.addObject(slopeObj);
            }
        }


//        System.out.println(testObjectSize +  " + " + lastSizeDrag);

        transformGroup.translate(Vec3.mult(center, -1), Space.WORLD);
        normalizedTransformGroup.rotate(currentObjectRotation.inverse());

        Transform gridTransform = new Transform();
        gridTransform.rotation.set(currentObjectRotation.inverse());

        Vec3 gridFacingAxis = Vec3.mult(orientationVec, 1);
        gridTransform.translate(Vec3.mult(Vec3.rotate(getCenterFromOrientation(currentOrientation), Transform.getRotationFromForward(gridFacingAxis)), -1), Space.SELF);

//        System.out.println(lastMouseClickPos);

        if (selectedMesh != null)
            testObject.mesh_ = selectedMesh.copy();
        else
        {
            testObject.mesh_ = new Mesh2();//PrimitiveFactory.createCube(20, 0);
            testObject.mesh_.material.baseColor = 0xFFFF0000;
        }

        boolean isWallChangingEnabled = input.isKeyDown(KeyEvent.VK_CONTROL);
        SelectionType currentSelectionType = SelectionType.NONE;
        if (controller.getSelectedObject().isPresent())
        {
            currentSelectionType = controller.getSelectedObject().get().getSelectionType();
        }

        if (currentSelectionType == SelectionType.WALL)
        {
            if (selectedAccTransform != null) {
                testObject.transform.position.set(selectedAccTransform.getWorldPosition());
                testObject.transform.rotation.set(selectedAccTransform.getWorldRotation());
                testObjectSize.set(testObject.transform.rotation.transform(new Vec3(selectedAccDimensions.x, selectedAccDimensions.y, 0)));
            }

            hoveredWallOrientation = controller.getSelectedWallOrientation().orElse(currentOrientation);
        }
        else if (currentSelectionType == SelectionType.ACCESSORY)
        {
            if (isSelectedObjectValid || !isWallChangingEnabled)
            {
                hoveredWallOrientation = controller.getSelectedAccessoryWallOrientation().orElse(null);
            }
        }



        Vec3 hoveredWallOrientationNormal = Vec3.up();
        Vec3 relativeCenter = center;
        if (hoveredWallOrientation != null)
        {
            relativeCenter = getCenterFromOrientation(hoveredWallOrientation);

            int[] normal = hoveredWallOrientation.getNormal();
            Vec3 normalVec = new Vec3(-normal[0], -normal[1], -normal[2]);
            hoveredWallOrientationNormal.set(currentObjectRotation.inverse().transform(normalVec));
        }


        Vec3 transl = Vec3.mult(Quaternion.fromDirection(hoveredWallOrientationNormal).transform(relativeCenter), -1);
        Plane dragPlane_ = new Plane(hoveredWallOrientationNormal, transl);

        if (selectedAccTransform != null)
        {
            testObject.selected = true;
            if (!prevHasSelectedAccessory)
            {

                testObject.transform.position.set(selectedAccTransform.getWorldPosition());
                testObject.transform.rotation.set(selectedAccTransform.getWorldRotation());
                testObjectSize.set(testObject.transform.rotation.transform(new Vec3(selectedAccDimensions.x, selectedAccDimensions.y, 0)));
                prevHasSelectedAccessory = true;
            }

        }
        else
        {
            prevHasSelectedAccessory = false;
        }

//        if (input.isMouseDragging(MouseEvent.BUTTON3) && input.isOnlyMouseButtonDown(MouseEvent.BUTTON3))
        if (input.getMouseDragMode() == MouseDragMode.OBJECT_POSITION_DRAGGING || input.getMouseDragMode() == MouseDragMode.OBJECT_SIZE_DRAGGING)
        {
            Vec3 worldPos = camera1.screenPointToPlane(currentMousePos, dragPlane_);

            Quaternion currentFacingRotation = Quaternion.fromDirection(dragPlane_.normal);

            Vec3 newSize = Vec3.sub(worldPos, lastSizeDrag);
            Vec3 transformPos = dragPlane_.closestPoint(Vec3.sub(worldPos, lastDragPos));
            if (controller.getGridToggle())
            {
                float gridSnapDist = controller.getGridDistance().getRawInchValueFloat()/10f;
                float correctionX = relativeCenter.x - MathUtils.snapToMultiple(relativeCenter.x, gridSnapDist);
                float correctionY = relativeCenter.y - MathUtils.snapToMultiple(relativeCenter.y, gridSnapDist);
                float correctionZ = relativeCenter.z - MathUtils.snapToMultiple(relativeCenter.z, gridSnapDist);
                transformPos.x = MathUtils.snapToMultiple(transformPos.x, gridSnapDist, correctionX);
                transformPos.y = MathUtils.snapToMultiple(transformPos.y, gridSnapDist, correctionY);
                transformPos.z = MathUtils.snapToMultiple(transformPos.z, gridSnapDist, correctionZ);

                newSize.x = Math.max(gridSnapDist, MathUtils.snapToMultiple(newSize.x, gridSnapDist, 0));
                newSize.y = Math.max(gridSnapDist, MathUtils.snapToMultiple(newSize.y, gridSnapDist, 0));
                newSize.z = Math.max(gridSnapDist, MathUtils.snapToMultiple(newSize.z, gridSnapDist, 0));
            }

//            System.out.println(worldPos + " + " + startPos);

            if (input.getMouseDragMode() == MouseDragMode.OBJECT_POSITION_DRAGGING && currentSelectionType != SelectionType.WALL)
            {
                testObject.transform.position.set(transformPos);
                testObject.transform.rotation.set(currentFacingRotation);
            }
            else if (input.getMouseDragMode() == MouseDragMode.OBJECT_SIZE_DRAGGING)
            {
                testObjectSize.set(newSize);
            }

            Vec3 localWallPos = currentFacingRotation.inverse().transform(testObject.transform.position);

            Vec3 localWallDimensions = currentFacingRotation.inverse().transform(testObjectSize);

//            System.out.println(relativeCenter + " to " + center);

            if (controller.getSelectedObject().isPresent())
            {
                if (hoveredWallOrientation != null && isWallChangingEnabled)
                {
                    if (hoveredWallOrientation != lastHoveredWallOrientation)
                    {
                        controller.changeSelectedAccessoryWall(controller.getWallDTOFromOrientation(hoveredWallOrientation).uuid);
                        lastHoveredWallOrientation = hoveredWallOrientation;
                    }
                }

                float xinch = Math.max(0, localWallPos.x + relativeCenter.x)*10f;
                float yinch = Math.max(0, localWallPos.y + relativeCenter.y)*10f;
                float winch = Math.max(0.01f, localWallDimensions.x) * 10f;
                float hinch = Math.max(0.01f, localWallDimensions.y) * 10f;

                Imperial ximp = Imperial.fromFloat(xinch);
                Imperial yimp = Imperial.fromFloat(yinch);
                Imperial wimp = Imperial.fromFloat(winch);
                Imperial himp = Imperial.fromFloat(hinch);

                if (controller.getGridToggle())
                {
                    Imperial gridVal = controller.getGridDistance();
                    ximp = ximp.roundToNearest(gridVal);
                    yimp = yimp.roundToNearest(gridVal);
                    wimp = wimp.roundToNearest(gridVal);
                    himp = himp.roundToNearest(gridVal);
                }

                try {
                    recenteringPosition = false;
                    if (currentSelectionType == SelectionType.ACCESSORY)
                    {
                        if (input.getMouseDragMode() == MouseDragMode.OBJECT_POSITION_DRAGGING)
                        {
                            if (!nextSaveAccPos)
                            {
                                controller.getSelectedAccessoryDTO().ifPresent(accessoryDTO -> {
                                    accOldPosValue = new Point2D<>(accessoryDTO.x, accessoryDTO.y);
                                });
                            }
                            var newValue = new Point2D<>(ximp, yimp);
                            controller.changeSelectedAccessoryPositionNoSave(newValue);
                            accSavePosValue = newValue;
                            nextSaveAccPos = true;
                        }
                        else
                        {
                            if (!nextSaveAccDim)
                            {
                                controller.getSelectedAccessoryDTO().ifPresent(accessoryDTO -> {
                                    accOldDimValue = new Dimensions<>(accessoryDTO.width, accessoryDTO.height);
                                });
                            }
                            var newValue = new Dimensions<>(wimp, himp);
                            controller.changeSelectedAccessoryDimensionsNoSave(newValue);
                            accSaveDimValue = newValue;
                            nextSaveAccDim = true;
                        }
                    }
                    else
                    {
                        if (input.getMouseDragMode() == MouseDragMode.OBJECT_SIZE_DRAGGING)
                        {

                            if (!nextSaveWallDim)
                            {
                                controller.getSelectedWallDTO().ifPresent(wallDTO -> {
                                    wallOldDimValue = new Dimensions<>(wallDTO.width, wallDTO.height);
                                        WallDTO[] orderedWalls = this.controller.getWallDTOCorrespondence(wallDTO.wallOrientation);

                                        accCorrespondingOldListValue.clear();
                                        accOtherOldListValue.clear();

                                for(int i = 0; i < orderedWalls.length;++i)
                                {
                                    if(i % 2 == 0)
                                    {
                                        accCorrespondingOldListValue.addAll(orderedWalls[i].accessories);
                                    }
                                    else
                                    {
                                        accOtherOldListValue.addAll(orderedWalls[i].accessories);
                                    }
                                }
                                });
                            }
                            var newValue = new Dimensions<>(wimp, himp);
                            controller.changeSelectedWallDimensionsNoSave(newValue);
                            wallSaveDimValue = newValue;
                            nextSaveWallDim = true;
                        }
                    }
                }
                catch (Exception ignored)
                {

                }
            }
        }
        else
        {
            Vec3 worldPos = camera1.screenPointToPlane(currentMousePos, dragPlane_);

            lastDragPos.set(Vec3.sub(worldPos, dragPlane_.closestPoint(testObject.transform.position)));
            lastSizeDrag.set(Vec3.sub(worldPos, testObjectSize));

            if (selectedAccTransform != null)
            {
                testObject.transform.position.set(selectedAccTransform.getWorldPosition());
                testObject.transform.rotation.set(selectedAccTransform.getWorldRotation());
                testObjectSize.set(testObject.transform.rotation.transform(new Vec3(selectedAccDimensions.x, selectedAccDimensions.y, 0)));
            }

            if (controller.getSelectedObject().isPresent())
            {
                if (nextSaveAccPos)
                {
                    controller.changeSelectedAccessoryPosition(accOldPosValue, accSavePosValue);
                    nextSaveAccPos = false;
                }
                if (nextSaveAccDim)
                {
                    controller.changeSelectedAccessoryDimensions(accOldDimValue, accSaveDimValue);
                    nextSaveAccDim = false;
                }
                if (nextSaveWallDim)
                {
                    controller.changeSelectedWallDimensions(wallOldDimValue, wallSaveDimValue, accCorrespondingOldListValue, accOtherOldListValue);
                    nextSaveWallDim = false;
                }
            }

        }
        testObject.transform.setScale(0.1f);
        scene.addObject(testObject);

//        Mesh teapotMesh = loadModel("/cube2.obj", 0xFFFFFFFF);

        // Create mesh
        Mesh2 mesh = new Mesh2();
        mesh.material.baseColor = 0xFFFFFFFF;

//        for (Vertex vertex : mesh.vertices) {
//            vertex.position.add(Vec3.mult(vertex.normal, new Vec3(1,1,1)).normalized());
//        }

//        totalMesh.setVertexScale(0.5f);
        Bounds meshBounds = totalMesh.calculateBounds();
        SceneObject centerObj = new SceneObject("centerobj", mesh);
//        centerObj.transform.rotate(currentObjectRotation);
//        centerObj.mesh_ = loadModel2("/teapot.obj", 0xFFFFFFFF);

//        Vec3 rotatedExtents = Vec3.rotate(meshBounds.extents, currentObjectRotation.toEuler());
        float boundsMag = meshBounds.extents.magnitude();

        float viewDistance = Math.abs(boundsMag)/10f;
//        centerObj.transform.position.z = -viewDistance;

        Vec3 relativeViewingCenter = getCenterFromOrientation(currentOrientation);

        viewingPoint.z = -Math.abs(relativeViewingCenter.z);

//        scene.addObject(centerObj);


//        float size = 0.25f;
//        int count = 40;
//        for (int i = 0; i < count; i++) {
//            for (int j = 0; j < count; j++) {
//                Mesh2 cubeMesh = PrimitiveFactory.createCube(size, 0xFFFFFFFF);
//                cubeMesh.material.baseColor = 0xFFFFFFFF;
//                SceneObject obj = new SceneObject(String.valueOf(i+j), cubeMesh);
//                obj.transform.translate(new Vec3(i*size, j*size, 0), Space.WORLD);
//                scene.addObject(obj);
//            }
//        }

        Mesh2 teapotMesh_ = totalMesh;//loadModel2("/testcube.obj", 0xFFFFFFFF);//PrimitiveFactory.createPlane2(0xFFFFFFFF);
        SceneObject teapotObject = new SceneObject("teapot-bruh", teapotMesh_);
        teapotObject.selectable = true;
        {
            Material meshMat = teapotObject.mesh_.material;//setColor(ColorUtils.setAlpha(hitObject.mesh.color, (int) (255*((Math.sin(timer*5)+1)/2))));
            meshMat.setRenderPass(RenderPass.OPAQUE);

            meshMat.baseColor = 0xFFFFFFFF;
//            BlurShader shader = new BlurShader();


            meshMat.setOnApplyMaterial(material -> {

//                textureList.push(new AWTARGBTexture(this.camera1View.getBufferedImage()));

//                ((BlurShader)meshMat.shader).samplers = textureList.getData();//new AWTTexture(this.camera1View.getBufferedImage());
//                ((BlurShader)meshMat.shader).depthTexture = new AWTFloatTexture(this.camera1View.getZBufferImage());
            });
//            meshMat.shader = shader;
            teapotMesh_.material = meshMat;
        }

        RenderStates teapotRenderState = teapotMesh_.material.getRenderStates();
        teapotRenderState.blend = false;

        teapotObject.mesh_ = teapotMesh_;
//        teapotObject.transform.rotation = camera1.transform.rotation;
//        teapotObject.transform.position = Vec3.add(camera1.transform.position, Vec3.mult(camera1.transform.getForward(), 3));
//        teapotObject.transform.rotate(currentObjectRotation.mult(Quaternion.fromEuler(new Vec3(0, (float) Math.toRadians(-0),0))));
//        teapotObject.transform.translate(new Vec3(0,0, (float) Math.sin(timer)*8f), Space.WORLD);

        teapotObject.transform.rotation.set(currentObjectRotation);

//        teapotObject.transform.rotation.set(camera1.transform.rotation);
//        teapotObject.transform.rotate(Quaternion.fromAxis(Vec3.up(), (float) Math.toRadians(180)));
//        scene.addObject(teapotObject);

        renderer.setScene(scene);
        renderer.mousePos = getScaledMousePos();

//        overlayRenderer.setOverlayScene(gridScene);

        if (controller.getGridToggle()) {
            float gridVisibility = (float) Math.pow(3, 10 * projectionAlpha - 10) - 0.8f;

            Vec3 v0 = camera1.screenToWorldPoint(new Vec3(0,0,0));
            Vec3 v1 = camera1.screenToWorldPoint(new Vec3(controller.getGridDistance().getRawInchValueFloat() / 10f,0,0));
            Vec3 distVec = Vec3.sub(v1, v0);
            gridVisibility = gridVisibility * (1 - distVec.magnitude());

            if (selectedObject != null)
            {
                gridVisibility = Math.max(gridVisibility, 0.025f);
            }
            if (gridVisibility > 0.025f) {
                createGrid(gridScene, camera1, controller.getGridDistance().getRawInchValueFloat() / 10f, gridTransform, gridFacingAxis, ColorUtils.rgba(1f, 1f, 1f, MathUtils.clampf(gridVisibility, 0, 1)));
            }
        }


//        overlayScene = new Scene();

        setupHUDOrienationCube((30*2)*RESOLUTION_FACTOR);

//        overlayScene.addObject(axis);


        // raycasting and end-of-update tasks

        Vec3 mousePosition = getScaledMousePos();

        Ray castRay = camera1.screenPointToRay(mousePosition, 100f, MathUtils.lerp(0.1f, -1f, projectionAlpha), MathUtils.lerp(1000f, 1f, projectionAlpha));
        java.util.List<SceneObject> blackList = new ArrayList<>();
        blackList.add(camera1);
        if (selectedObject != null)
            blackList.add(selectedObject);
        raycastList = scene.queryFromRay(castRay, blackList);

        if (hoveredObject != null)
        {
            float visib = dimAxisTween.update(dt);
            displayObjectDimensions(hoveredObject, 10f*zoomLevel, 3f, 7.5f*zoomLevel, ColorUtils.setAlpha(0xFFFFFFFF, (int) (255f*visib)));
        }
        else
        {
            dimAxisTween.reset();
        }

        if (!raycastList.isEmpty())
        {
            Iterator<RaycastResult> it = raycastList.iterator();
//            RESOLUTION_FACTOR = 0.5f;
            SceneObject hitObject = it.next().target;

            if (hitObject.transform == selectedTransform)
            {
                if (it.hasNext())
                    hitObject = it.next().target;
            }

            hoveredObject = hitObject;
            hoveredPoint = raycastList.get(0).hitPoint;
            if (hitObject.selectable)
            {
                if (!input.isKeyDown(KeyEvent.VK_SHIFT) && prevLeftMouseClicked)//&& input.isMouseButtonDown(MouseEvent.BUTTON1) && !prevMouseLeftClick)
                {
                    if (selectObjectInternal(hitObject) && selectedObject == null)
                    {
//                        objectDragStart.set(Vec3.add(currentMousePos, camera1.worldToScreenPoint(selectedTransform.position)));
                    }
                    prevMouseLeftClick = true;
                }
            }
            selectObject(hitObject);


        }
        else {
            hoveredObject = null;
            hoveredPoint = null;
            if (!input.isKeyDown(KeyEvent.VK_SHIFT) && prevLeftMouseClicked)//input.isMouseButtonDown(MouseEvent.BUTTON1) && !prevMouseLeftClick)
            {
                selectObjectInternal(null);
                prevMouseLeftClick = true;
            }
        }

        Ray hudRay = hudCamera.screenPointToRay(mousePosition, 100f, -100f, 1000f);
        overlayRaycastList = overlayScene.queryFromRay(hudRay);
        if (!overlayRaycastList.isEmpty())
        {
            Iterator<RaycastResult> it = overlayRaycastList.iterator();
            SceneObject hitObject = it.next().target;

            if (!input.isAnyMouseButtonDown())
            {
                hitObject.notifyHover();
            }

            if (hitObject.selectable)
            {
                if (!input.isKeyDown(KeyEvent.VK_SHIFT) && prevLeftMouseClicked)//&& input.isMouseButtonDown(MouseEvent.BUTTON1) && !prevMouseLeftClick)
                {
                    hitObject.notifyEvent();
                    prevMouseLeftClick = true;
                }
                hitObject.selected = true;
            }
//            selectObject(hitObject);

        }


        if (isHoveringSelectedObject && input.isMouseDragging(MouseEvent.BUTTON1))
        {
            if (input.isKeyDown(KeyEvent.VK_SHIFT))
                input.updateMouseDragMode(MouseDragMode.OBJECT_SIZE_DRAGGING);
            else
                input.updateMouseDragMode(MouseDragMode.OBJECT_POSITION_DRAGGING);
        }
        else if (input.isMouseDragging(MouseEvent.BUTTON1) && input.isKeyDown(KeyEvent.VK_SHIFT))
        {
            input.updateMouseDragMode(MouseDragMode.ORBITTING);
        }
        else if (input.isMouseDragging(MouseEvent.BUTTON2) || input.isMouseDragging(MouseEvent.BUTTON1))
        {
            input.updateMouseDragMode(MouseDragMode.PANNING);
        }

        lastMousePos.set(currentUnscaledMousePos);
        // need to reset mouse drag for when the mouse stops moving
        input.resetMouseDrag();
        input.resetScrollDelta();

        prevLeftMouseClicked = false;
        prevRightMouseClicked = false;

        if (triggerLeftClickNextFrame)
        {
            prevLeftMouseClicked = true;
            triggerLeftClickNextFrame = false;
        }
        if (triggerRightClickNextFrame)
        {
            prevRightMouseClicked = true;
            triggerRightClickNextFrame = false;
        }


        if (!input.isAnyMouseButtonDown())
        {
            input.resetMouseDragMode();
        }
    }

    private void setupHUDOrienationCube(float size) {
        float axesDist = size;

        SceneObject axis = new HUDObject("Axis", PrimitiveFactory.createCube(axesDist, 0xFFFFFFFF));//loadModel2("/axis.obj", 0xFFFFFFFF));
        axis.mesh_.material.baseColor = 0xff626262;
        axis.transform.setScale(1f);
        axis.transform.translate(new Vec3(scaledWidth/2 - axesDist*2,scaledHeight/2 - axesDist*2, 0), Space.WORLD);
        axis.transform.rotation.set(Quaternion.mult(currentObjectRotation,camera1.transform.rotation).inverse());

        Bounds bounds = axis.mesh_.calculateBounds();


        Vec3[] axes = new Vec3[]{
                Vec3.forward(axesDist*1.125f),
                Vec3.up(axesDist*1.125f),
                Vec3.right(-axesDist*1.125f),
                Vec3.up(-axesDist*1.125f),
                Vec3.right(axesDist*1.125f),
                Vec3.forward(-axesDist*1.125f),
        };

        Orientation[] targetOrientations = new Orientation[]{
                Orientation.RIGHT,
                Orientation.TOP,
                Orientation.BACK,
                null,
                Orientation.FRONT,
                Orientation.LEFT,
        };

        {
            Vec3 min = bounds.min;
            Vec3 max = bounds.max;

            Vec3[] vertices = new Vec3[8];
            vertices[0] = new Vec3(min.x, max.y, max.z);
            vertices[1] = new Vec3(min.x, min.y, max.z);
            vertices[2] = new Vec3(max.x, min.y, max.z);
            vertices[3] = new Vec3(max.x, max.y, max.z);

            vertices[4] = new Vec3(max.x, max.y, min.z);
            vertices[5] = new Vec3(max.x, min.y, min.z);
            vertices[6] = new Vec3(min.x, min.y, min.z);
            vertices[7] = new Vec3(min.x, max.y, min.z);

            // Define the indices to create quads
            int[][] indices = {
                    {3, 2, 1, 0}, // Front
                    {7, 4, 3, 0}, // Right
                    {0, 1, 6, 7}, // Top
                    {6, 1, 2, 5}, // Left
                    {4, 5, 2, 3}, // Bottom
                    {7, 6, 5, 4}  // Back
            };


            for (int i = 0; i < indices.length; i++) {
                int[] index = indices[i];
                Vec3 v0 = vertices[index[0]];
                Vec3 v1 = vertices[index[1]];
                Vec3 v2 = vertices[index[2]];
                Vec3 v3 = vertices[index[3]];
                SceneObject face = new HUDObject("axis face", PrimitiveFactory.createQuad(v0, v1, v2, v3, 0));
                face.mesh_.material.baseColor = 0xff626262;
                StrokeShader shader = new StrokeShader();
                shader.sampler = faceTextures[i];
                shader.strokeColor = ColorUtils.vec4(0xffa7a7a7);
                face.mesh_.material.shader = shader;
                face.transform.set(axis.transform);

                face.setObjectHoverListener(() -> {
                    shader.colorMult = new Vec4(1.25f,1.25f,1.25f,1f);
                });
                if (targetOrientations[i] != null)
                {
                    int finalI = i;
                    face.setObjectEventListener(() -> this.controller.changeOrientation(targetOrientations[finalI]));
                }
                overlayScene.addObject(face);
            }
        }


        for (int i = 0; i < axes.length; i++) {
            if (i == 3)
                continue;

            Vec3 axis_ = axes[i];
            Material handleMat = new Material();
            handleMat.baseColor = 0xff626262;
            handleMat.getRenderStates().blend = true;
            handleMat.setRenderPass(RenderPass.TRANSPARENT);
            handleMat.getRenderStates().cullFace = false;
            handleMat.shader = new ArrowShader();

            SceneObject handle = new HUDObject("handle", PrimitiveFactory.createQuad(axesDist / 2, 0));
            handle.mesh_.material = handleMat;
            float dot = Math.abs(Vec3.dot(Vec3.forward(), axis.transform.rotation.transform(axis_.normalized())));
            float handleVisib = (1f - dot * 1.25f);
            if (handleVisib > 0.1f) {

                handle.mesh_.material.baseColor = ColorUtils.setAlpha(handle.mesh_.material.baseColor, (int) (255 * handleVisib));

                handle.transform.position.set(axis.transform.position);
                handle.transform.translate(axis.transform.rotation.transform(axis_), Space.WORLD);

                Vec3 directionToCube = Vec3.sub(axis.transform.position, handle.transform.position).normalized();
                Quaternion lookRotation = Quaternion.lookAt(new Vec3(0, 0, 0), directionToCube, Vec3.forward(-1));
                Vec3 forward = lookRotation.transform(Vec3.forward(-1));
                Vec3 up = lookRotation.transform(Vec3.up(-1));
                Quaternion finalRotation = Quaternion.lookAt(new Vec3(0, 0, 0), forward, up);
                Quaternion initialOffsetRotation = Quaternion.fromEuler(new Vec3((float) Math.toRadians(90), 0, 0));
                finalRotation = Quaternion.mult(finalRotation, initialOffsetRotation);

                handle.transform.rotation.set(finalRotation);
//                handle.transform.rotation.set(Quaternion.fromDirection(Vec3.sub(handle.transform.position, axis.transform.position).normalized()));
//                handle.transform.rotation.set(Quaternion.fromAxis(Vec3.forward(), Vec3.angleBetween(plane.closestPoint(handle.transform.position), plane.closestPoint(axis.transform.position))));
//                handle.transform.rotation.set(Quaternion.fromAxis(Vec3.forward(), -axis.transform.rotation.toEuler().z));//Quaternion.fromDirection(Vec3.sub(handle.transform.position, axis.transform.position).normalized()));

                handle.setObjectHoverListener(() -> {

                    ((ArrowShader)handleMat.shader).colorMult = new Vec4(1.25f,1.25f,1.25f,1f);
                });

                if (targetOrientations[i] != null)
                {
                    int finalI = i;
                    handle.setObjectEventListener(() -> this.controller.changeOrientation(targetOrientations[finalI]));
                }

                overlayScene.addObject(handle);
            }
        }
    }

    private SceneObject create2DLine(Vec3 start, Vec3 end, float width, int argb)
    {
        SceneObject square = new SceneObject("Plane", PrimitiveFactory.createPlane2(argb));
        RenderStates renderStates = square.mesh_.material.getRenderStates();
        renderStates.depthTest = false;
        renderStates.blend = true;
//        renderStates.skipSpaceConversion = true;
        renderStates.cullFace = false;
        renderStates.cullPlanes = false;

        square.mesh_.material.baseColor = argb;
        square.mesh_.material.setRenderPass(RenderPass.OVERLAY);
        square.mesh_.material.shader = new UnlitShader();


        square.shaded = false;

        Vec3 startPoint = start;
        Vec3 endPoint = end;

        Vec3 direction = Vec3.sub(endPoint, startPoint);

        float scale = direction.magnitude();
        square.transform.setScale(new Vec3(scale, width, 1f));

        float angle = (float) Math.atan2(direction.y, direction.x);
        square.transform.rotate(new Vec3(0, 0, angle));

        Vec3 translate = Vec3.mult(Vec3.right,scale/2);
        square.transform.translate(Vec3.add(startPoint, Vec3.rotate(translate, new Vec3(0,0,angle))), Space.WORLD);

        return square;
    }

    private HUDObject create2DLineForOverlay(Vec3 start, Vec3 end, float width, int argb)
    {
        HUDObject square = new HUDObject("Plane", PrimitiveFactory.createPlane2(argb));
        RenderStates renderStates = square.mesh_.material.getRenderStates();
        renderStates.depthTest = false;
        renderStates.blend = true;
//        renderStates.skipSpaceConversion = true;
        renderStates.cullFace = false;
        renderStates.cullPlanes = false;

        square.mesh_.material.baseColor = argb;
        square.mesh_.material.setRenderPass(RenderPass.OVERLAY);
        square.mesh_.material.shader = new UnlitShader();


        square.shaded = false;

        Vec3 startPoint = Vec3.sub(Vec3.mult(start, new Vec3(1,-1,0)), new Vec3(scaledWidth/2, -scaledHeight/2, 0));
        Vec3 endPoint = Vec3.sub(Vec3.mult(end, new Vec3(1,-1,0)), new Vec3(scaledWidth/2, -scaledHeight/2, 0));

        Vec3 direction = Vec3.sub(endPoint, startPoint);

        float scale = direction.magnitude();
        square.transform.setScale(new Vec3(scale, width, 1f));

        float angle = (float) Math.atan2(direction.y, direction.x);
        square.transform.rotate(new Vec3(0, 0, angle));

        Vec3 translate = Vec3.mult(Vec3.right,scale/2);
        square.transform.translate(Vec3.add(startPoint, Vec3.rotate(translate, new Vec3(0,0,angle))), Space.WORLD);

        return square;
    }


    private void createGrid(Scene scene, Camera mainCamera, float gridSpacing, Transform gridTransform, Vec3 gridFacingAxis, int argb)
    {
        float distance = mainCamera.worldToScreenDistance(1,gridTransform.position.z);
        float worldWidth = (scaledWidth/distance);
        float worldHeight = (scaledHeight/distance);
        int gridLeft = (int) ((worldWidth/2 + gridTransform.position.x - faceViewLookAtPoint.x + 1) / gridSpacing);
        int gridRight = (int) ((worldWidth/2 - gridTransform.position.x + faceViewLookAtPoint.x + 1) / gridSpacing);
        int gridTop = (int) ((worldHeight/2 - gridTransform.position.y + faceViewLookAtPoint.y + 1) / gridSpacing);
        int gridBottom = (int) ((worldHeight/2 + gridTransform.position.y - faceViewLookAtPoint.y + 1) / gridSpacing);


        Vec3 gridRotation = Transform.getRotationFromForward(gridFacingAxis);

        gridTransform.rotate(getSceneRotationFromOrientation(currentOrientation));


        Vec3 normal = Vec3.mult(gridTransform.getForward(), 1);

        float shading = Math.max(0f, Vec3.dot(mainCamera.transform.getForward().normalized(), normal.normalized()));

        shading = (float) Math.sqrt(shading);

        int gridColor = argb;
        int alpha = (int)(((gridColor >> 24) & 0xff) * shading);
        int red = (int) (((gridColor >> 16) & 0xff) * shading);
        int green = (int) (((gridColor >> 8) & 0xff) * shading);
        int blue = (int) (((gridColor >> 0) & 0xff) * shading);
        gridColor = ((alpha & 0xFF) << 24) |
                ((red & 0xFF) << 16) |
                ((green & 0xFF) << 8)  |
                ((blue & 0xFF) << 0);

        for (int i = -gridBottom; i <= gridTop; i++) {
            Vec3 startPoint = new Vec3(-gridLeft * gridSpacing, i * gridSpacing, 0);
            Vec3 endPoint = new Vec3(gridRight * gridSpacing, i * gridSpacing, 0);

            startPoint = gridTransform.getTransform().transform(startPoint.toVec4()).toVec3();
            endPoint = gridTransform.getTransform().transform(endPoint.toVec4()).toVec3();

            startPoint = mainCamera.worldToScreenPoint(startPoint);
            endPoint = mainCamera.worldToScreenPoint(endPoint);

            var lineobj = create2DLine(startPoint, endPoint, 1, gridColor);
            lineobj.mesh_.material.getRenderStates().skipSpaceConversion = true;
            scene.addObject(lineobj);
        }

        for (int j = -gridLeft; j <= gridRight; j++) {
            Vec3 startPoint = new Vec3(j * gridSpacing, -gridBottom * gridSpacing, 0);
            Vec3 endPoint = new Vec3(j * gridSpacing, gridTop * gridSpacing, 0);

            startPoint = gridTransform.getTransform().transform(startPoint.toVec4()).toVec3();
            endPoint = gridTransform.getTransform().transform(endPoint.toVec4()).toVec3();

            startPoint = mainCamera.worldToScreenPoint(startPoint);
            endPoint = mainCamera.worldToScreenPoint(endPoint);

            var lineobj = create2DLine(startPoint, endPoint, 1, gridColor);
            lineobj.mesh_.material.getRenderStates().skipSpaceConversion = true;
            scene.addObject(lineobj);
        }
    }

    private void render() {

        bufferStrategy = getBufferStrategy();
        if (bufferStrategy == null) {
            BufferCapabilities bufferCaps = new BufferCapabilities(
                    new ImageCapabilities(true), // You can specify various image capabilities
                    new ImageCapabilities(true), // Front buffer capabilities
                    BufferCapabilities.FlipContents.BACKGROUND // Flip contents
            );
            createBufferStrategy(2);
            return;
        }
        Graphics g = null;



        camera1.calculateViewMatrix();
        hudCamera.calculateViewMatrix();

        do {
            try {
                g = bufferStrategy.getDrawGraphics();

                // clear
                g.clearRect(0, 0, width, height);
//                image.getGraphics().clearRect(0, 0, scaledWidth, scaledHeight);
//                Graphics g2 = (zBufferImage.getGraphics());
//                g2.setColor(new Color(0xFFFFFFFF));
//                g2.fillRect(0, 0, scaledWidth, scaledHeight);

                renderer.setView(camera1View);
                renderer.setMainCamera(camera1);

                ClearStates clearStates = new ClearStates();
                clearStates.colorFlag = true;
                clearStates.depthFlag = true;
                clearStates.stencilFlag = true;

                clearStates.clearColor = bgColor;
                clearStates.clearDepth = 1.0f;
                clearStates.clearStencil = 0;

                renderer.render(clearStates);

                // render HUD
                renderer.setMainCamera(null);
                renderer.setScene(gridScene);
                // dont clear color
                clearStates.colorFlag = false;
                renderer.render(clearStates);


//                orthoCam.setOrthographicRect((float) -camera1View.getWidth(), 0, (float) camera1View.getHeight(), 0,-1000, 1000);
                hudCamera.setOrthographic(camera1View.getHeight() / 2.0f, (float) camera1View.getWidth(), (float) camera1View.getHeight(), -100.0f, 1000);

                renderer.setMainCamera(hudCamera);
                renderer.setScene(overlayScene);
                // dont clear color
                clearStates.colorFlag = false;
                renderer.render(clearStates);

                for (StringDrawable stringDrawable : this.stringsToDraw) {
                    Graphics graphics = this.camera1View.getBufferedImage().getGraphics();
                    graphics.setColor(new Color(stringDrawable.argb, true));
                    graphics.drawString(stringDrawable.text, (int) stringDrawable.screenPosition.x, (int) stringDrawable.screenPosition.y);
                }
                this.stringsToDraw.clear();

//                this.camera1View.postProcessingTest = true;
//                renderer.render();

//                overlayRenderer.render();

//                this.camera1View.applyCRTShader(0.5f,0.1f,0.5f,0.75f,0.25f,0.15f);

//                renderer.setView(camera2View);
////                this.camera1.setOrthographic(20.0f, (float)camera1View.getWidth(), (float)camera1View.getHeight(), 1.0f, 1000.0f);
////
//                renderer.setMainCamera(camera2);
//
//
//                renderer.render();



                BufferedImage view1BufferedImage = this.camera1View.getBufferedImage();
                BufferedImage view2BufferedImage = this.camera2View.getBufferedImage();
                g.drawImage(view1BufferedImage, 0, 0, width, height, 0, 0, scaledWidth, scaledHeight, null);
//                g.drawImage(view1BufferedImage, 0, 0, null);
//                g.drawImage(camera1View.getStencilBufferImage(), 0, 0, scaledWidth/4, scaledHeight/4, 0, 0, scaledWidth, scaledHeight, null);
//                g.drawImage(view2BufferedImage, 0, 0, null);
                g.drawString(String.valueOf(fps) + " fps\n mouseDrag: " + input.getMouseDragDeltaX() + ", " + input.getMouseDragDeltaY() + ", " + renderingStats + ", " + scaledWidth + "x" + scaledHeight + ", zoom: " + zoomLevel, 10, 20);
//                g.setColor(Color.WHITE);
//                g.drawLine(scaledWidth, 0, scaledWidth, height);
                renderingStats.reset();
            } finally {
                assert g != null;
                g.dispose();
            }
            bufferStrategy.show();
        } while (bufferStrategy.contentsLost());
    }

    public Vec3 getScaledMousePos()
    {
        Vec2 mousePos = input.getMousePosition();
        Vec3 mousePosition = new Vec3(0,0,0);
        if (mousePos != null)
        {
            mousePosition.x = mousePos.x*RESOLUTION_FACTOR;
            mousePosition.y = mousePos.y*RESOLUTION_FACTOR;
        }
        return mousePosition;
    }

    public Vec3 getUnscaledMousePos()
    {
        Vec2 mousePos = input.getMousePosition();
        Vec3 mousePosition = new Vec3(0,0,0);
        if (mousePos != null)
        {
            mousePosition.x = mousePos.x;
            mousePosition.y = mousePos.y;
        }
        return mousePosition;
    }

    private Vec2 getViewDimensionsFromViewOrientation(Orientation orientation)
    {
        Vec2 dimensions = new Vec2(0, 0);
        if (orientation == Orientation.TOP)
        {
            WallDTO[] wallDTOs = controller.getWallDTOs();
            float viewX = wallDTOs[0].width.getRawInchValueFloat() / 10f;
            float viewY = wallDTOs[1].width.getRawInchValueFloat() / 10f;
            dimensions.set(viewX, viewY);
        }
        else
        {
            WallDTO wallDTO = controller.getWallDTOFromOrientation(orientation);
            ExtensionDTO extensionDTO = controller.getExtensionDTO();
            float viewX = wallDTO.width.getRawInchValueFloat() / 10f;
            float viewY = wallDTO.height.getRawInchValueFloat() / 10f + extensionDTO.getHeight().getRawInchValueFloat() / 10f;
            dimensions.set(viewX, viewY);
        }

        return dimensions;
    }

    void resetFaceViewMode()
    {
        faceViewMode = true;
        isDoneTransitioning = false;
        recenteringRotation = true;
        recenteringPosition = true;

        startLerpPosition.set(camera1.transform.position);
        startLerpLookAtPoint.set(cameraLookAtPoint);
        startLerpCamRotation.set(camera1.transform.rotation);
        startLerpObjectRotation.set(currentObjectRotation);
        startFixedViewDimensions.set(fixedViewDimensions);
        startFaceViewLookAtPoint.set(faceViewLookAtPoint);
        startZoomLevel = zoomLevel;

        tween.reset();
    }

    void zoomFaceView(float zoomDelta)
    {
        Camera tempCam = new Camera("temp camera");
        tempCam.transform.set(camera1.transform);

        Vec3 mousePos = getScaledMousePos();
        tempCam.setOrthographic(10.0f, (float) camera1View.getWidth(), (float) camera1View.getHeight(), 0.1f, 1000.0f);
        Vec3 zoomOrigin = tempCam.screenToWorldPoint(mousePos);
        tempCam.setOrthographic(10.0f * zoomDelta, (float) camera1View.getWidth(), (float) camera1View.getHeight(), 0.1f, 1000.0f);
        Vec3 newZoomOrigin = tempCam.screenToWorldPoint(mousePos);

        Vec3 offset = Vec3.sub(newZoomOrigin, zoomOrigin);

        recenteringPosition = false;
        faceViewLookAtPoint.add(offset);
//        cameraLookAtPoint.add(offset);
    }

    void calculateCameras()
    {
        //        this.camera1.setOrthographicFromObjectDimensions(fixedViewDimensions.x*1f, fixedViewDimensions.y * 1f, camera1View.getWidth(), camera1View.getHeight());
//            this.camera1.setOrthographic(controller.getWallDTOs()[0].height.getRawInchValueFloat()/10, (float) camera1View.getWidth(), (float) camera1View.getHeight(), 0.1f, 1000.0f);
        this.camera1.setOrthographic(10.0f * zoomLevel, (float) camera1View.getWidth(), (float) camera1View.getHeight(), 0.1f, 1000.0f);

//        this.camera1.setOrthographicRect((float) -camera1View.getWidth()/2, (float) camera1View.getWidth()/2, (float) -camera1View.getHeight()/2, (float) camera1View.getHeight()/2,-1, 1000);
        this.camera1.setPerspective((float) Math.toRadians(90), (float) camera1View.getWidth(), (float) camera1View.getHeight(), 0.1f, 1000f);
//        this.camera2.setPerspective((float)Math.toRadians(90), (float)camera2View.getWidth(), (float)camera2View.getHeight(), 1.0f, 1000.0f);
        this.camera2.setOrthographic(30.0f, (float) camera1View.getWidth(), (float) camera1View.getHeight(), 1.0f, 1000.0f);
//        this.camera1.setOrthographic(-20.0f, (float)camera1View.getWidth(), (float)camera1View.getHeight(), 1.0f, 1000.0f);
        camera1.projectionLerp(ProjectionType.PERSPECTIVE, ProjectionType.ORTHOGRAPHIC, projectionAlpha);
    }

    private float adjustZoomIncrement(float zoomIncrement, float currentZoomLevel) {
        float zoomFactor = (float) Math.sqrt(currentZoomLevel);
        return zoomIncrement * zoomFactor;
    }

    private float getOrthoSizeForDimensions(float width, float height, float screenWidth, float screenHeight)
    {
        float aspectRatio = width / height;
        float screenRatio = screenWidth / screenHeight;

        float orthoSize;

        if (aspectRatio > screenRatio) {
            orthoSize = width / 2;
        } else {
            orthoSize = height / 2;
        }

        return orthoSize;
    }

    private int getWallColor(WallDTO wallDTO)
    {
        return switch (wallDTO.wallOrientation) {
            case BACK -> 0xFF0288D8;
            case FRONT -> 0xFF0288D8;
            case RIGHT -> 0xFFFF8C00;
            case LEFT -> 0xFFFF8C00;
            default -> 0xFFFFFFFF;
        };
    }

    private boolean selectObjectInternal(SceneObject sceneObject)
    {
        try {
            if (sceneObject == null)
            {
                controller.changeSelectedObject(new Selectable(null, "", SelectionType.NONE));
                return false;
            }

            UUID uuid = (UUID) sceneObject.getUserData();
            Selectable selectable = controller.getSelectableFromUUID(uuid);

            if (controller.getSelectedObject().isPresent() && controller.getSelectedObject().get().getUUID() == selectable.getUUID())
            {
                controller.changeSelectedObject(new Selectable(null, "", SelectionType.NONE));
            }
            else
            {
                controller.changeSelectedObject(selectable);
            }
            return true;
        }
        catch (Exception ignored) {
            return false;
        }
    }

    private void selectObject(SceneObject sceneObject)
    {
        //            hitObject.transform.scale.mult(2);
        Material meshMat = sceneObject.mesh_.material;//setColor(ColorUtils.setAlpha(hitObject.mesh.color, (int) (255*((Math.sin(timer*5)+1)/2))));
        meshMat.getRenderStates().blend = true;
        meshMat.getRenderStates().depthTest = true;
        meshMat.setRenderPass(RenderPass.TRANSPARENT);

        meshMat.baseColor = ColorUtils.setAlpha(meshMat.baseColor, (int) (100 + 155*((Math.sin(timer*5)+1)/2)));
        BlurShader shader = new BlurShader();
        meshMat.setOnApplyMaterial(material -> {

            textureList.push(new AWTARGBTexture(this.camera1View.getBufferedImage()));

            ((BlurShader)material.shader).samplers = textureList.getData();//new AWTTexture(this.camera1View.getBufferedImage());

        });
        sceneObject.selected = true;

//            shader.sampler = new AWTTexture(this.camera1View.getBufferedImage());
        meshMat.shader = shader;
    }

    private boolean isObjectSelected(String descriptor)
    {
        return this.raycastList.stream().anyMatch(raycastResult -> raycastResult.target.descriptor.equals(descriptor));
    }

    private Vec3 getCenterFromOrientation(Orientation orientation)
    {
        float centerX;
        float centerY;
        float centerZ;
        try {
            WallDTO[] correspondence = this.controller.getWallDTOCorrespondence(orientation);

            WallDTO frontWall = correspondence[0];
            WallDTO sideWall = correspondence[1];

            centerX = (frontWall.width.getRawInchValueFloat() / 2f) / 10f;
            centerY = ((frontWall.height.getRawInchValueFloat() + controller.getExtensionDTO().getHeight().getRawInchValueFloat()) / 2f) / 10f;
            centerZ = (sideWall.width.getRawInchValueFloat() / 2f) / 10f;
        }
        catch (IllegalArgumentException e) {
            WallDTO[] walls = this.controller.getWallDTOs();
            centerX = (walls[1].width.getRawInchValueFloat() / 2f) / 10f;
            centerY = (walls[1].height.getRawInchValueFloat() / 2f) / 10f;
            centerZ = (walls[2].width.getRawInchValueFloat() / 2f) / 10f;
        }

        return new Vec3(centerX, centerY, centerZ);
    }

    private Quaternion getSceneRotationFromOrientation(Orientation orientation)
    {
        return switch (orientation) {
            case FRONT -> Quaternion.fromAxis(Vec3.up(), (float) Math.toRadians(-90));
            case LEFT -> Quaternion.fromAxis(Vec3.up(), (float) Math.toRadians(0));
            case BACK -> Quaternion.fromAxis(Vec3.up(), (float) Math.toRadians(90));
            case RIGHT -> Quaternion.fromAxis(Vec3.up(), (float) Math.toRadians(180));
            case TOP -> Quaternion.fromAxis(Vec3.right(), (float) Math.toRadians(90)).mult(Quaternion.fromAxis(Vec3.forward(), (float) Math.toRadians(90)));
        };
    }

    private void displayObjectDimensions(SceneObject object, float axisDist, float axisThickness, float stacheSize, int argb)
    {
        Mesh2 objectMesh = object.mesh_;
        Bounds accessoryMeshBounds = objectMesh.calculateBounds();

        int measurementsColor = argb;
        {
            Vec3 sidePos = new Vec3(accessoryMeshBounds.min.x, accessoryMeshBounds.max.y + axisDist, accessoryMeshBounds.min.z);
            {
                Vec3 sideStache1 = Vec3.add(sidePos, new Vec3(0,stacheSize/2,0));
                Vec3 sideStache2 = Vec3.add(sidePos, new Vec3(0,-stacheSize/2,0));
                Vec3 worldStache1 = object.transform.getTransform().transform(sideStache1.toVec4()).toVec3();
                Vec3 worldStache2 = object.transform.getTransform().transform(sideStache2.toVec4()).toVec3();
                overlayScene.addObject(create2DLineForOverlay(camera1.worldToScreenPoint(worldStache1), camera1.worldToScreenPoint(worldStache2), axisThickness, measurementsColor));

            }

            Vec3 sidePos2 = new Vec3(accessoryMeshBounds.max.x, accessoryMeshBounds.max.y + axisDist, accessoryMeshBounds.min.z);
            {
                Vec3 sideStache1 = Vec3.add(sidePos2, new Vec3(0,stacheSize/2,0));
                Vec3 sideStache2 = Vec3.add(sidePos2, new Vec3(0,-stacheSize/2,0));
                Vec3 worldStache1 = object.transform.getTransform().transform(sideStache1.toVec4()).toVec3();
                Vec3 worldStache2 = object.transform.getTransform().transform(sideStache2.toVec4()).toVec3();
                overlayScene.addObject(create2DLineForOverlay(camera1.worldToScreenPoint(worldStache1), camera1.worldToScreenPoint(worldStache2), axisThickness, measurementsColor));
            }
            Vec3 worldStartPos = object.transform.getTransform().transform(sidePos.toVec4()).toVec3();
            Vec3 worldEndPos = object.transform.getTransform().transform(sidePos2.toVec4()).toVec3();
            overlayScene.addObject(create2DLineForOverlay(camera1.worldToScreenPoint(worldStartPos), camera1.worldToScreenPoint(worldEndPos), axisThickness, measurementsColor));

            {
                Vec3 textPos = new Vec3(accessoryMeshBounds.center.x, accessoryMeshBounds.max.y + stacheSize + axisDist, 0);
                textPos = object.transform.getTransform().transform(textPos.toVec4()).toVec3();
                stringsToDraw.add(new StringDrawable(camera1.worldToScreenPoint(textPos), Imperial.fromFloat(Math.max(0.1f, accessoryMeshBounds.size.x)).toString(), measurementsColor));
            }
        }
        {
            Vec3 sidePos = new Vec3(accessoryMeshBounds.max.x + axisDist, accessoryMeshBounds.max.y, accessoryMeshBounds.min.z);
            {
                Vec3 sideStache1 = Vec3.add(sidePos, new Vec3(stacheSize/2,0,0));
                Vec3 sideStache2 = Vec3.add(sidePos, new Vec3(-stacheSize/2,0,0));
                Vec3 worldStache1 = object.transform.getTransform().transform(sideStache1.toVec4()).toVec3();
                Vec3 worldStache2 = object.transform.getTransform().transform(sideStache2.toVec4()).toVec3();
                overlayScene.addObject(create2DLineForOverlay(camera1.worldToScreenPoint(worldStache1), camera1.worldToScreenPoint(worldStache2), axisThickness, measurementsColor));
            }
            Vec3 sidePos2 = new Vec3(accessoryMeshBounds.max.x + axisDist, accessoryMeshBounds.min.y, accessoryMeshBounds.min.z);
            {
                Vec3 sideStache1 = Vec3.add(sidePos2, new Vec3(stacheSize/2,0,0));
                Vec3 sideStache2 = Vec3.add(sidePos2, new Vec3(-stacheSize/2,0,0));
                Vec3 worldStache1 = object.transform.getTransform().transform(sideStache1.toVec4()).toVec3();
                Vec3 worldStache2 = object.transform.getTransform().transform(sideStache2.toVec4()).toVec3();
                overlayScene.addObject(create2DLineForOverlay(camera1.worldToScreenPoint(worldStache1), camera1.worldToScreenPoint(worldStache2), axisThickness, measurementsColor));
            }
            Vec3 worldStartPos = object.transform.getTransform().transform(sidePos.toVec4()).toVec3();
            Vec3 worldEndPos = object.transform.getTransform().transform(sidePos2.toVec4()).toVec3();
            overlayScene.addObject(create2DLineForOverlay(camera1.worldToScreenPoint(worldStartPos), camera1.worldToScreenPoint(worldEndPos), axisThickness, measurementsColor));

            {
                Vec3 textPos = new Vec3(accessoryMeshBounds.max.x + (stacheSize + axisDist), accessoryMeshBounds.center.y, 0);
                textPos = object.transform.getTransform().transform(textPos.toVec4()).toVec3();
                stringsToDraw.add(new StringDrawable(camera1.worldToScreenPoint(textPos), Imperial.fromFloat(Math.max(0.1f, accessoryMeshBounds.size.y)).toString(), measurementsColor));
            }
        }

    }

}



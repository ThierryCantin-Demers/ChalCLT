package ca.ulaval.glo2004.rendering;

import ca.ulaval.glo2004.rendering.utils.Plane;
import ca.ulaval.glo2004.rendering.utils.Ray;
import ca.ulaval.glo2004.util.math.*;

public class Camera extends SceneObject {

    private ProjectionType projectionType = ProjectionType.PERSPECTIVE;

    private float viewWidth;
    private float viewHeight;

    private float perspectiveFOV = (float) Math.toRadians(90.0f);
    private float perspectiveAspectRatio = 1.0f;
    private float perspectiveNear = 0.01f;
    private float perspectiveFar = 1000.0f;
    private Mat4 perspectiveProjection = Mat4.identity();

    private float orthographicSize = 100.0f;
    private float orthographicNear = -1.0f;
    private float orthographicFar = 200.0f;

    private float orthographicLeft = 200f;
    private float orthographicRight = 200f;
    private float orthographicBottom = 200f;
    private float orthographicTop = 200f;
    private boolean orthographicPerfect = false;

    private Mat4 orthographicProjection = Mat4.identity();

    private Mat4 currentProjection = Mat4.identity();

    private Mat4 viewMatrix = Mat4.identity();

    public Camera(String name)
    {
        super(name, new Mesh(new Triangle[]{}));
        this.transform = new Transform();
        calculateProjectionMatrix();
    }

    public void projectionLerp(ProjectionType from, ProjectionType to, float alpha)
    {
        alpha = MathUtils.clampf(alpha, 0, 1);

        Mat4 matFrom = null;
        Mat4 matTo = null;

        switch (from)
        {
            case PERSPECTIVE:
                matFrom = perspectiveProjection;
                break;
            case ORTHOGRAPHIC:
                matFrom = orthographicProjection;
        }

        switch (to)
        {
            case PERSPECTIVE:
                matTo = perspectiveProjection;
                break;
            case ORTHOGRAPHIC:
                matTo = orthographicProjection;
        }

        currentProjection = Mat4.lerp(matFrom, matTo, alpha);
    }

    public ProjectionType getProjectionType()
    {
        return projectionType;
    }

    public Mat4 getProjectionMatrix() {
        return currentProjection;
    }

    public Mat4 getViewMatrix() {
        return viewMatrix;
    }

    public void calculateViewMatrix()
    {
        this.viewMatrix = this.transform.getTransform().inverse();
    }

    public void setPerspective(float fov, float viewWidth, float viewHeight, float near, float far)
    {
        this.perspectiveFOV = fov;
        this.perspectiveNear = near;
        this.perspectiveFar = far;

        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;

        this.perspectiveAspectRatio = viewWidth / viewHeight;
        projectionType = ProjectionType.PERSPECTIVE;
        calculateProjectionMatrix();
        this.currentProjection = this.perspectiveProjection;


//        // Define the vertices of the cube
//        Vec3[] vertices = new Vec3[8];
//
//// Near plane points
//        vertices[0] = new Vec3((float) (-near * tan(fov / 2) * aspectRatio), (float) (-near * tan(fov / 2)), near);
//        vertices[1] = new Vec3((float) (near * tan(fov / 2) * aspectRatio), (float) (-near * tan(fov / 2)), near);
//        vertices[2] = new Vec3((float) (near * tan(fov / 2) * aspectRatio), (float) (near * tan(fov / 2)), near);
//        vertices[3] = new Vec3((float) (-near * tan(fov / 2) * aspectRatio), (float) (near * tan(fov / 2)), near);
//
//// Far plane points
//        vertices[4] = new Vec3((float) (-far * tan(fov / 2) * aspectRatio), (float) (-far * tan(fov / 2)), far);
//        vertices[5] = new Vec3((float) (far * tan(fov / 2) * aspectRatio), (float) (-far * tan(fov / 2)), far);
//        vertices[6] = new Vec3((float) (far * tan(fov / 2) * aspectRatio), (float) (far * tan(fov / 2)), far);
//        vertices[7] = new Vec3((float) (-far * tan(fov / 2) * aspectRatio), (float) (far * tan(fov / 2)), far);
//
//
//        int color = 0xFF00FF00;
//        // Define the triangles of the cube
//        Triangle[] triangles = new Triangle[]{
//                // Near plane
//                new Triangle(vertices[0], vertices[1], vertices[2], color),
//                new Triangle(vertices[0], vertices[2], vertices[3], color),
//                // Far plane, color
//                new Triangle(vertices[4], vertices[5], vertices[6], color),
//                new Triangle(vertices[4], vertices[6], vertices[7], color),
//                // Sides, color
//                new Triangle(vertices[0], vertices[4], vertices[5], color),
//                new Triangle(vertices[0], vertices[5], vertices[1], color),
//                new Triangle(vertices[1], vertices[5], vertices[6], color),
//                new Triangle(vertices[1], vertices[6], vertices[2], color),
//                new Triangle(vertices[2], vertices[6], vertices[7], color),
//                new Triangle(vertices[2], vertices[7], vertices[3], color),
//                new Triangle(vertices[3], vertices[7], vertices[4], color),
//                new Triangle(vertices[3], vertices[4], vertices[0], color)
//        };
//
//        Mesh cameraMesh = new Mesh(triangles);

        Vec3[] cameraVertices = new Vec3[]{
                new Vec3(0, 0, 0 + 0.5f),      // Camera tip (apex)
                new Vec3(0, 0, -1.5f + 0.5f),  // Arrow tail (camera direction)
                new Vec3(0.2f, 0, -0.5f + 0.5f),   // Right bottom point of the arrowhead
                new Vec3(-0.2f, 0, -0.5f + 0.5f),  // Left bottom point of the arrowhead
                new Vec3(0, 0.2f, -0.5f + 0.5f),   // Top point of the arrowhead
                new Vec3(0, -0.2f, -0.5f + 0.5f)  // Bottom point of the arrowhead
        };

        Triangle[] cameraTriangles = new Triangle[]{
                // Arrowhead bottom triangles
                new Triangle(cameraVertices[2], cameraVertices[4], cameraVertices[3], 0xFF00FF00), // Green
                new Triangle(cameraVertices[2], cameraVertices[5], cameraVertices[4], 0xFF00FF00), // Green
                new Triangle(cameraVertices[2], cameraVertices[3], cameraVertices[5], 0xFF00FF00), // Green
                new Triangle(cameraVertices[3], cameraVertices[4], cameraVertices[5], 0xFF00FF00), // Green

                // Arrowhead side triangles
                new Triangle(cameraVertices[0], cameraVertices[2], cameraVertices[4], 0xFFFF0000), // Red
                new Triangle(cameraVertices[0], cameraVertices[5], cameraVertices[2], 0xFFFF0000), // Red
                new Triangle(cameraVertices[0], cameraVertices[4], cameraVertices[5], 0xFFFF0000), // Red
                new Triangle(cameraVertices[0], cameraVertices[3], cameraVertices[4], 0xFFFF0000), // Red
        };

// Create a mesh for the 3D camera arrow with increased height
        Mesh cameraMesh = new Mesh(cameraTriangles);


        this.mesh = cameraMesh;
    }

    public void setOrthographic(float size, float viewWidth, float viewHeight, float near, float far)
    {
        this.orthographicSize = size;
        this.orthographicNear = near;
        this.orthographicFar = far;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;


        float aspectRatio = viewWidth / viewHeight;
        this.orthographicLeft = -orthographicSize * aspectRatio;
        this.orthographicRight = orthographicSize * aspectRatio;
        this.orthographicBottom = -orthographicSize;
        this.orthographicTop = orthographicSize;
        this.orthographicPerfect = false;

        projectionType = ProjectionType.ORTHOGRAPHIC;
        calculateProjectionMatrix();
        this.currentProjection = this.orthographicProjection;
    }

    public void setOrthographicRect(float left, float right, float bottom, float top, float near, float far)
    {
        this.orthographicNear = near;
        this.orthographicFar = far;

        this.orthographicLeft = left;//-orthographicSize * aspectRatio;
        this.orthographicRight = right;//orthographicSize * aspectRatio;
        this.orthographicBottom = bottom;//-orthographicSize;
        this.orthographicTop = top;//orthographicSize;
        this.orthographicPerfect = true;
        projectionType = ProjectionType.ORTHOGRAPHIC;
        calculateProjectionMatrix();
        this.currentProjection = this.orthographicProjection;
    }


    public void setOrthographicFromObjectDimensions(float objectWidthInWorld, float objectHeightInWorld, float screenWidth, float screenHeight) {
//        // Calculate the aspect ratio of the object
//        float objectAspectRatio = objectWidthInWorld / objectHeightInWorld;
//
//        // Calculate the aspect ratio of the screen view
//        float screenAspectRatio = screenWidth / screenHeight;
//
//        // Determine the larger aspect ratio (either object or screen)
//        float largerAspectRatio = Math.max(objectAspectRatio, screenAspectRatio);
//
//        // Adjust the orthographic size to fit the object within the screen view
//        orthographicSize = largerAspectRatio > 1 ? objectWidthInWorld / 2 : objectHeightInWorld / 2;
//
//        // Adjust orthographic bounds based on the determined orthographic size
//        orthographicLeft = -orthographicSize * screenAspectRatio;
//        orthographicRight = orthographicSize * screenAspectRatio;
//        orthographicBottom = -orthographicSize;
//        orthographicTop = orthographicSize;
//        projectionType = ProjectionType.ORTHOGRAPHIC;
//
//        calculateProjectionMatrix(); // Recalculate the projection matrix

        // Calculate the ratio of object dimensions to screen dimensions
        float aspectRatio = objectWidthInWorld / objectHeightInWorld;
        float screenRatio = screenWidth / screenHeight;

        float orthoSize;

        if (aspectRatio > screenRatio) {
            // Object is wider than the screen
            orthoSize = objectWidthInWorld / 2; // Adjust as needed
        } else {
            // Object is taller than the screen
            orthoSize = objectHeightInWorld / 2; // Adjust as needed
        }

        // Set orthographic projection with the calculated size
        setOrthographic(orthoSize, screenWidth, screenHeight, orthographicNear, orthographicFar);
    }

    public void zoom(float zoomFactor)
    {
        // Calculate the new zoomed size (adjust based on zoomDelta)
        float newOrthoSize = orthographicSize * zoomFactor;
        float newFOV = perspectiveFOV * zoomFactor;

        // Update camera parameters based on the zoom factor
//        if (projectionType == ProjectionType.PERSPECTIVE) {
            perspectiveFOV = newFOV;
//        } else if (projectionType == ProjectionType.ORTHOGRAPHIC) {
            orthographicSize = newOrthoSize;
//        }
        calculateProjectionMatrix();
    }

    public void zoomOrtho(float zoomFactor)
    {
        // Calculate the new zoomed size (adjust based on zoomDelta)
        float newOrthoSize = orthographicSize * zoomFactor;
        orthographicSize = newOrthoSize;
        setOrthographic(orthographicSize, viewWidth, viewHeight, orthographicNear, orthographicFar);
    }

    public void zoomAroundMouse(Vec2 mouseScreenPos, float zoomFactor) {
        float zoomDelta = 1;

        // Convert mouse position to world coordinates
        Vec3 mouseWorldPos = screenToWorldPoint(mouseScreenPos.toVec3());

        // Calculate the new zoomed size (adjust based on zoomDelta)
        float newOrthoSize = orthographicSize * zoomFactor;
        float newFOV = perspectiveFOV * zoomFactor;

        // Calculate the translation vector to move the camera towards the mouse position
        Vec3 translation = Vec3.sub(transform.position, mouseWorldPos);
        translation.mult(zoomDelta); // Scale the translation based on the zoom change

        // Translate the camera towards the mouse position
        transform.position.add(translation);

        // Update camera parameters based on the zoom factor
        if (projectionType == ProjectionType.PERSPECTIVE) {
//            perspectiveFOV = newFOV;
        } else if (projectionType == ProjectionType.ORTHOGRAPHIC) {
            orthographicSize = newOrthoSize;
        }

        // Recalculate the projection matrix with updated parameters
        calculateProjectionMatrix();
    }

    public float screenToWorldDistance(int x, float depth)
    {
        Vec3 worldPoint1 = screenToWorldPoint(new Vec3(0,0,depth));
        Vec3 worldPoint2 = screenToWorldPoint(new Vec3(x,0,depth));

        return Vec3.sub(worldPoint1, worldPoint2).magnitude();
    }

    public float worldToScreenDistance(int x, float depth)
    {
        Vec3 screenPoint1 = worldToScreenPoint(new Vec3(0,0,depth));
        Vec3 screenPoint2 = worldToScreenPoint(new Vec3(x,0,depth));

        return Vec3.sub(screenPoint1, screenPoint2).magnitude();
    }


    public Vec3 worldToScreenPoint(Vec3 point)
    {
        Mat4 cameraTransform = transform.getTransform();
        Mat4 viewTransform = cameraTransform.inverse();
        Mat4 projectionTransform = getProjectionMatrix();

        Vec4 viewedPoint = viewTransform.transform(point.toVec4());
        Vec4 projectedPoint = projectionTransform.transform(viewedPoint);

        projectedPoint.mult(1/projectedPoint.w);

        Vec3 screenPoint = projectedPoint.toVec3();

        screenPoint.mult(new Vec3(-1,-1,1));

        Vec3 offsetView = new Vec3(1,1,0);

        screenPoint.add(offsetView);

        float widthScale = 0.5f * viewWidth;
        float heightScale = 0.5f * viewHeight;
        screenPoint.mult(new Vec3(widthScale, heightScale, 1));

        return screenPoint;
//        return boundToScreen(screenPoint);
    }

    public Vec3 boundToScreen(Vec3 point)
    {
        point.x = Math.max(0, Math.min(viewWidth - 1, point.x));
        point.y = Math.max(0, Math.min(viewHeight - 1, point.y));
        point.z = Math.max(-1, Math.min(1, point.z));
        return point;
    }

    public Vec3 screenToWorldPoint(Vec3 point)
    {
        Mat4 inverseViewTransform = transform.getTransform();
        Mat4 inverseProjectionTransform = getProjectionMatrix().inverse();

        float widthScale = 2.0f / viewWidth;
        float heightScale = 2.0f / viewHeight;
        Vec3 offsetView = new Vec3(-1, -1, 0);

        Vec3 newPoint = new Vec3(0,0,0);
        newPoint.set(point);
        newPoint.mult(new Vec3(widthScale, heightScale, 1));
        newPoint.add(offsetView);

        newPoint.mult(new Vec3(-1,-1,1));

        Vec4 projectedPoint = newPoint.toVec4();
        projectedPoint.w = 1.0f;

        Vec4 viewedPoint = inverseProjectionTransform.transform(projectedPoint);

        Vec3 worldPoint = inverseViewTransform.transform(viewedPoint).toVec3();

        worldPoint.mult(1/viewedPoint.w);

        return worldPoint;
    }

    public Vec3 worldDragCoordinates(Vec2 startMousePos, Vec2 currentMousePos) {
        Vec3 startWorldPos = screenToWorldPoint(new Vec3(startMousePos.x, startMousePos.y, 0));
        Vec3 currentWorldPos = screenToWorldPoint(new Vec3(currentMousePos.x, currentMousePos.y, 0));

        Vec3 dragVector = Vec3.sub(currentWorldPos, startWorldPos);

        return dragVector; // You can return this vector for further manipulation if needed
    }

    public Ray screenPointToRay(Vec3 point, float length, float near, float far)
    {
        // todo eventually change 0.1 and 1000 to camera's current near and far planes
        Vec3 worldPoint = screenToWorldPoint(new Vec3(point.x, point.y, near));
        Vec3 endWorldPoint = screenToWorldPoint(new Vec3(point.x, point.y, far));

        Vec3 rayDirection = Vec3.sub(endWorldPoint, worldPoint).normalized();

        Ray ray = new Ray(worldPoint, rayDirection, length);

        return ray;
    }


    public Ray screenPointToRay(Vec3 point, float length)
    {
        return screenPointToRay(point, length, 0.1f, 1000f);
    }

    public Vec3 screenPointToPerspectivePlaneIntersection(Vec3 screenPoint)
    {
        Ray ray = screenPointToRay(screenPoint,0);
        Plane plane = new Plane(this.transform.getForward(), 0);
        float dist = plane.raycast(ray).orElse(0.0f);
        return ray.getPoint(dist);
    }

    public Vec3 screenPointToPlane(Vec3 screenPoint, Plane plane)
    {
        Ray ray = screenPointToRay(screenPoint,0);
        float dist = plane.raycast(ray).orElse(0.0f);
        return ray.getPoint(dist);
    }

    private void calculateProjectionMatrix()
    {
//        if (projectionType == ProjectionType.PERSPECTIVE)
        {

            float n = perspectiveNear; // Near clipping plane distance
            float f = perspectiveFar; // Far clipping plane distance
            float fov = perspectiveFOV; // Field of view in radians
            float aspectRatio = perspectiveAspectRatio;


            float t = n * (float) Math.tan(fov / 2);
            float b = -t;
            float r = -t * aspectRatio;
            float l = -r;

//            float fov = (float) (1.0f/ tan(perspectiveFOV*0.5f));




            this.perspectiveProjection = new Mat4(new float[][]{

                    {2 * n / (r - l), 0, (r + l) / (r - l), 0},
                    {0, 2 * n / (t - b), (t + b) / (t - b), 0},
                    {0, 0, -(f + n) / (f - n), -2 * f * n / (f - n)},
                    {0, 0, 1, 0}

            });
        }
//        else if (projectionType == ProjectionType.ORTHOGRAPHIC)
        {
//            float aspectRatio = (float)viewWidth / viewHeight;
//            float orthoSize = orthographicSize; // You can adjust this value based on your needs
            float left = orthographicLeft;//-orthoSize * aspectRatio;
            float right = orthographicRight;//orthoSize * aspectRatio;
            float bottom = orthographicBottom; //-orthoSize;
            float top = orthographicTop;//orthoSize;

            float near = orthographicNear; // Near clipping plane distance
            float far = orthographicFar; // Far clipping plane distance

// Map z-values from -1 to 1
            float zScale = 2.0f / (far - near);
            float zBias = -(far + near) / (far - near);

            float tx = -(right + left) / (right - left);
            float ty = -(top + bottom) / (top - bottom);
            float tz = zBias;

            Mat4 orthoProjection = new Mat4(new float[][]{
                    {-2 / (right - left), 0, 0, tx},
                    {0, 2 / (top - bottom), 0, ty},
                    {0, 0, zScale, tz},
                    {0, 0, 0, 1}
            });

            this.orthographicProjection = orthoProjection;
        }

    }


}

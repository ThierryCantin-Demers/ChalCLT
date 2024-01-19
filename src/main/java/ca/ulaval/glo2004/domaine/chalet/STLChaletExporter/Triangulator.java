package ca.ulaval.glo2004.domaine.chalet.STLChaletExporter;

import ca.ulaval.glo2004.domaine.chalet.IAccessory;
import ca.ulaval.glo2004.domaine.chalet.dto.AccessoryDTO;
import ca.ulaval.glo2004.domaine.chalet.dto.WallDTO;
import ca.ulaval.glo2004.domaine.Orientation;
import ca.ulaval.glo2004.util.math.Imperial;
import ca.ulaval.glo2004.util.math.Vec2;
import ca.ulaval.glo2004.util.math.Vec3;

import java.util.ArrayList;
import java.util.List;

import static ca.ulaval.glo2004.util.math.Vec2.add;
import static java.lang.Math.max;
import static java.util.Collections.sort;


public class Triangulator {
    private WallDTO frontWall;
    private WallDTO leftWall;
    private WallDTO backWall;
    private WallDTO rightWall;

    private Imperial imprecesion;

    public Triangulator(WallDTO[] walls, Imperial imprecision) throws IllegalStateException {

        for (WallDTO wall : walls) {
            boolean allAccessoriesAreValid = wall.accessories.stream().allMatch(x -> x.isValid);
            if(!allAccessoriesAreValid)
            {
                throw new IllegalStateException("Le chalet courant contient des murs invalides");
            }
        }// peut etre a refactor, j'aime pas devoir valider les mures moi meme dans Triangulator
        assert (walls[0].wallOrientation == Orientation.FRONT);
        assert (walls[1].wallOrientation == Orientation.LEFT);
        assert (walls[2].wallOrientation == Orientation.BACK);
        assert (walls[3].wallOrientation == Orientation.RIGHT);
        this.frontWall = walls[0];
        this.leftWall = walls[1];
        this.backWall = walls[2];
        this.rightWall = walls[3];
        this.imprecesion = imprecision;
    }
    public List<STLTriangle> triangulateFrontWallWithHoles()
    {
        return triangulateWallWithHolesDTO(frontWall);
    }

    private List<STLTriangle> triangulateWallWithHolesDTO(WallDTO wall) {
        ArrayList<STLTriangle> triangleList = new ArrayList<>();
        //inside the chalet
        Vec2 v0 = new Vec2(0, 0);
        Vec2 v1 = new Vec2(wall.width.minus(imprecesion.divide(2)).getRawInchValueFloat(), 0);
        Vec2 v2 = add(v1, new Vec2(0, wall.height.getRawInchValueFloat()));
        Vec2 v3 = add(v0, new Vec2(0, wall.height.getRawInchValueFloat()));

        List<Float> importantPointsX = new ArrayList<>();
        List<Float> importantPointsY = new ArrayList<>();
        //adding four corners of the face in there
        importantPointsX.add(v0.x);
        importantPointsY.add(v0.y);
        importantPointsX.add(v2.x);
        importantPointsY.add(v2.y);

        for (AccessoryDTO acc : wall.accessories) {
            float a0x = acc.x.minus(imprecesion.divide(2)).getRawInchValueFloat();
            if (!importantPointsX.contains(a0x)) {
                importantPointsX.add(a0x);
            }
            float a0y;
            try{
                a0y = acc.y.minus(imprecesion.divide(2)).getRawInchValueFloat();
            }
            catch(IllegalArgumentException e)
            {
                a0y = 0;
            }
            if (!importantPointsY.contains(a0y)) {
                importantPointsY.add(a0y);
            }
            float a1x = acc.x.plus(acc.width).minus(imprecesion.divide(2)).getRawInchValueFloat();
            if (!importantPointsX.contains(a1x)) {
                importantPointsX.add(a1x);
            }
            float a1y = acc.y.plus(acc.height).minus(imprecesion.divide(2)).getRawInchValueFloat();
            if (!importantPointsY.contains(a1y)) {
                importantPointsY.add(a1y);
            }
        }
        sort(importantPointsX);
        sort(importantPointsY);
        for (int y = 0; y < importantPointsY.size() - 1; y++) {
            for (int x = 0; x < importantPointsX.size() - 1; x++) {
                boolean isInsideHole = false;
                float x0 = importantPointsX.get(x);
                float x1 = importantPointsX.get(x + 1);
                float y0 = importantPointsY.get(y);
                float y1 = importantPointsY.get(y + 1);
                for (AccessoryDTO acc : wall.accessories
                ) {
                    float a0x = acc.x.minus(imprecesion.divide(2)).getRawInchValueFloat();
                    float a0y;
                    try {
                        a0y = acc.y.minus(imprecesion.divide(2)).getRawInchValueFloat();
                    }
                    catch(IllegalArgumentException e)
                    {
                        a0y = 0;
                    }
                    float a1x = acc.x.plus(acc.width).minus(imprecesion.divide(2)).getRawInchValueFloat();
                    float a1y = acc.y.plus(acc.height).minus(imprecesion.divide(2)).getRawInchValueFloat();
                    if (isColliding(x0, x1, y0, y1, a0x, a1x, a0y, a1y)) {
                        isInsideHole = true;
                        break;
                    }
                }
                if (!isInsideHole) {
                    Vec3 bottom_left_front = new Vec3(x0, y0, 0);
                    Vec3 bottom_left_inside = new Vec3(x0, y0, wall.thickness.getRawInchValueFloat());
                    Vec3 bottom_right_front = new Vec3(x1, y0, 0);
                    Vec3 bottom_right_inside = new Vec3(x1, y0, wall.thickness.getRawInchValueFloat());
                    Vec3 top_left_front = new Vec3(x0, y1, 0);
                    Vec3 top_left_inside = new Vec3(x0, y1, wall.thickness.getRawInchValueFloat());
                    Vec3 top_right_front = new Vec3(x1, y1, 0);
                    Vec3 top_right_inside = new Vec3(x1, y1, wall.thickness.getRawInchValueFloat());
                    STLTriangle[] front_triangles = makeRectangleTriangles(top_left_front, bottom_left_front, top_right_front, bottom_right_front);
                    STLTriangle[] inside_triangles = makeRectangleTriangles(top_right_inside, bottom_right_inside, top_left_inside, bottom_left_inside);
                    triangleList.add(front_triangles[0]);
                    triangleList.add(front_triangles[1]);
                    triangleList.add(inside_triangles[0]);
                    triangleList.add(inside_triangles[1]);
                }
                //check if is inside a hole
            }
        }
        return triangleList;
    }

    private boolean isColliding(float x0, float x1, float y0, float y1, float a0x, float a1x, float a0y, float a1y) {
        if (x1 <= a0x || a1x <= x0) {
            return false;
        }

        // Check for no overlap in y-axis
        if (y1 <= a0y || a1y <= y0) {
            return false;
        }

        return true;
    }

//   private List<Vec2> exteriorFaceWithHoles();

    private STLTriangle[] makeRectangleTriangles(Vec3 topLeft, Vec3 bottomLeft, Vec3 topRight, Vec3 bottomRight) {
        STLTriangle[] triangles = new STLTriangle[2];
        triangles[0] = new STLTriangle(topLeft, bottomLeft, bottomRight);
        triangles[1] = new STLTriangle(topLeft, bottomRight, topRight);
        return triangles;
    }

}

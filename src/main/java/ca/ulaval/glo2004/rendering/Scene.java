package ca.ulaval.glo2004.rendering;

import ca.ulaval.glo2004.rendering.utils.Ray;
import ca.ulaval.glo2004.util.math.Vec3;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    List<SceneObject> sceneObjects;

    public Scene()
    {
        sceneObjects = new ArrayList<>();
    }

    public void addObject(SceneObject sceneObject)
    {
        this.sceneObjects.add(sceneObject);
    }

    public List<SceneObject> getSceneObjects() {
        return sceneObjects;
    }

    public List<RaycastResult> queryFromRay(Ray ray)
    {
        return queryFromRay(ray, new ArrayList<>());
    }

    public List<RaycastResult> queryFromRay(Ray ray, List<SceneObject> blacklist)
    {
        List<RaycastResult> result = new ArrayList<>();
        for (SceneObject sceneObject : sceneObjects) {
            if (blacklist.contains(sceneObject))
                continue;

            sceneObject.raycast(ray).ifPresent(result::add);
        }
        result.sort((r1, r2) -> {
            Vec3 dist1 = Vec3.sub(r1.hitPoint, ray.startPoint);
            Vec3 dist2 = Vec3.sub(r2.hitPoint, ray.startPoint);
            return Float.compare(dist1.magnitude(), dist2.magnitude());
        });
        return result;
    }
}

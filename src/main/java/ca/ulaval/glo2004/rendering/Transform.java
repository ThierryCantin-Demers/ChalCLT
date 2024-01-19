package ca.ulaval.glo2004.rendering;

import ca.ulaval.glo2004.util.math.*;

import java.util.ArrayList;
import java.util.List;

public class Transform {

    private Transform parent = null;
    private List<Transform> children = new ArrayList<>();

    public Vec3 position;
    public Quaternion rotation;
    public Vec3 scale;

    public Transform() {
        this.position = new Vec3(0, 0, 0);
        this.rotation = Quaternion.identity();
        this.scale = new Vec3(1, 1, 1);
    }

    public void setParent(Transform parent)
    {
        this.parent = parent;
    }

    public Transform getParent()
    {
        return this.parent;
    }

    public List<Transform> getChildren() {
        return children;
    }

    public void addChild(Transform child)
    {
        child.setParent(this);
        children.add(child);
    }

    public void removeChild(Transform child)
    {
        int res = children.indexOf(child);
        if (res != -1)
        {
            child.setParent(null);
            children.remove(child);
        }
    }

    public void set(Transform other)
    {
        this.position.set(other.position);
        this.rotation.set(other.rotation);
        this.scale.set(other.scale);
    }

    public void translate(Vec3 translation, Space relativeTo)
    {
        if (relativeTo == Space.WORLD)
        {
            if (parent != null)
            {
                position.add(Mat4.scale(parent.scale).inverse().transform(translation.toVec4()).toVec3());
            }
            else
            {
                position.add(translation);
            }
        }
        else
        {
            Mat4 rotationMatrix = rotation.toRotationMatrix();
            Vec4 localTranslation = rotationMatrix.transform(new Vec4(translation.x, translation.y, translation.z, 1.0f));
            position.add(new Vec3(localTranslation.x, localTranslation.y, localTranslation.z));
        }
    }

    public void rotate(Vec3 amount, float sensitivity)
    {
        // apply mouse sensitivity
        amount.mult(sensitivity);

        // create orientation vectors
        Vec3 up = Vec3.up();
        Vec3 lookat = getForward();
        Vec3 forward = new Vec3(lookat.x, 0, lookat.z).normalized();
        Vec3 side = Vec3.cross(up, forward);

        // rotate camera with quaternions created from axis and angle
        rotation = Quaternion.mult(Quaternion.fromAxis(up, amount.y), rotation);
        rotation = Quaternion.mult(Quaternion.fromAxis(side, amount.x), rotation);
        rotation = Quaternion.mult(Quaternion.fromAxis(forward, amount.z), rotation);

        // Ensure pitch stays within -90 and 90 degrees
        float pitch = MathUtils.clampf(getPitch(), -90 + MathUtils.FLOAT_ROUNDING_ERROR, 90 - MathUtils.FLOAT_ROUNDING_ERROR);
        rotation = Quaternion.fromEuler(new Vec3(pitch, getYaw(), 0));
    }

    // Assuming you have the following methods in your class
    private float getPitch() {
        return rotation.toEuler().x;
    }

    private float getYaw() {
        return rotation.toEuler().y;
    }

    private float getRoll() {
        return rotation.toEuler().z;
    }

    public void rotate(Vec3 rotation) {
        this.rotation.mult(Quaternion.fromEuler(rotation));
    }
    public void rotate(Quaternion rotation) {
        this.rotation.mult(rotation);
    }

    public void setScale(Vec3 scale) {
        this.scale = scale;
    }

    public void setScale(float scale) {
        this.scale = new Vec3(scale, scale, scale);
    }

    public void follow(Transform targetTransform, float distance, float height, float smoothness) {
        // Calculate the target position based on the target's forward direction
        Vec3 targetPosition = Vec3.add(targetTransform.position, Vec3.mult(targetTransform.getForward(), -distance));
        targetPosition = Vec3.add(targetPosition, Vec3.mult(Vec3.up, height));

        // Use Vec3.lerp to smoothly move the camera towards the target position
        position = Vec3.lerp(position, targetPosition, smoothness);

        // Assuming you have a method like "lookAt" in your Transform class, use it to make the camera face the target
        lookAt(targetTransform.position);
    }
    public void lookAt(Vec3 worldPosition) {
        // Calculate the forward direction from the transform's position to the target world position
        Vec3 forward = Vec3.sub(worldPosition, position).normalized();

        // Calculate the new rotation based on the forward vector
        rotation = Quaternion.lookAt(position, worldPosition, Vec3.up);
    }

    public static Vec3 getRotationFromForward(Vec3 forward) {
        // Compute the rotation angles (pitch and yaw) based on the forward vector
        float pitch = (float) Math.asin(-forward.y);
        float yaw = (float) Math.atan2(forward.x, forward.z);

        return new Vec3(pitch, yaw, 0);
    }

    public Mat4 lookAtMatrix(Vec3 target) {
        Vec3 forward = Vec3.sub(target, position).normalized();
        Vec3 a = Vec3.mult(forward, Vec3.dot(Vec3.up, forward));
        Vec3 up = Vec3.sub(Vec3.up, a).normalized();

        Vec3 right = Vec3.cross(up, forward).normalized();

        Mat4 matrix = new Mat4(new float[][]{
                {right.x, right.y, right.z, 0.0f},
                {up.x, up.y, up.z, 0.0f},
                {forward.x, forward.y, forward.z, 0.0f},
                {0,0,0, 1.0f}
        });
        return matrix;
    }

    public Vec3 getUp()
    {
        // Calculate the up vector based on the rotation
        return rotation.transform(Vec3.up);
    }

    public Vec3 getRight()
    {
        // Calculate the right vector based on the rotation
        return rotation.transform(Vec3.right);
    }

    public Vec3 getForward()
    {
        // Calculate the forward vector based on the rotation
        return rotation.transform(Vec3.forward);
    }

    public Vec3 getWorldPosition() {
        Vec3 truePosition = position.copy();

        Transform currentParent = parent;
        while (currentParent != null) {
            truePosition = currentParent.rotation.transform(truePosition);
            truePosition.mult(currentParent.scale);
            truePosition.add(currentParent.position);

            currentParent = currentParent.getParent();
        }

        return truePosition;
    }

    public Quaternion getWorldRotation() {
        Quaternion trueRotation = rotation.copy();

        Transform currentParent = parent;
        while (currentParent != null) {
            trueRotation = Quaternion.mult(currentParent.rotation, trueRotation);

            currentParent = currentParent.getParent();
        }

        return trueRotation;
    }

    public Mat4 getTransform()
    {
        Mat4 rotationMatrix = rotation.toRotationMatrix();
        Mat4 translationMatrix = Mat4.translation(position);
        Mat4 scaleMatrix = Mat4.scale(scale);

        Mat4 localTransform = translationMatrix.multiply(rotationMatrix).multiply(scaleMatrix);

        if (parent != null)
        {
            return parent.getTransform().multiply(localTransform);
        }

        return localTransform;
    }


}

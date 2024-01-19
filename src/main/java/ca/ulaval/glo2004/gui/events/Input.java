package ca.ulaval.glo2004.gui.events;

import ca.ulaval.glo2004.util.math.Vec2;

import java.awt.event.*;

public class Input implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    private boolean[] pressedKeys = new boolean[68836];
    public boolean[] mouseButtons = new boolean[5];
    private int mouseX, mouseY;
    private int lastMouseX, lastMouseY;
    private int mouseDragDeltaX, mouseDragDeltaY;

    private int draggingButton = -1;

    private MouseClickedEvent onMouseClicked = e -> {};
    private MouseReleasedEvent onMouseReleased = e -> {};
    private KeyEventListener onKeyEvent = e -> {};
    private int scrollDeltaX;
    private int scrollDeltaY;

    private MouseDragMode mouseDragMode = MouseDragMode.NONE;

    private boolean[] mouseButtonDragging = new boolean[5];

    public void setOnMouseClicked(MouseClickedEvent onMouseClicked) {
        this.onMouseClicked = onMouseClicked;
    }

    public void setOnMouseReleased(MouseReleasedEvent onMouseReleased) {
        this.onMouseReleased = onMouseReleased;
    }

    public void setOnKeyEvent(KeyEventListener onKeyEvent) {
        this.onKeyEvent = onKeyEvent;
    }

    public boolean updateMouseDragMode(MouseDragMode mouseDragMode)
    {
        if (this.mouseDragMode == MouseDragMode.NONE)
        {
            this.mouseDragMode = mouseDragMode;
            return true;
        }
        else return this.mouseDragMode == mouseDragMode;
    }

    public void resetMouseDragMode()
    {
        this.mouseDragMode = MouseDragMode.NONE;
    }

    public MouseDragMode getMouseDragMode() {
        return mouseDragMode;
    }

    public boolean isKeyDown(int keyCode) {
        if (keyCode >= 0 && keyCode < pressedKeys.length)
            return pressedKeys[keyCode];
        return false;
    }

    public boolean isMouseDragging(int button) {
        if (button >= 0 && button < mouseButtons.length)
            return mouseButtonDragging[button];
        return false;
    }

    public boolean isOnlyMouseButtonDown(int button) {
        if (button >= 0 && button < mouseButtons.length) {
            if (mouseButtons[button]) {
                for (int i = 0; i < mouseButtons.length; i++) {
                    if (i != button && mouseButtons[i]) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean isAnyMouseButtonDown()
    {
        for (boolean mouseButton : mouseButtons) {
            if (mouseButton)
                return true;
        }
        return false;
    }

    public boolean isMouseButtonDown(int button) {
        if (button >= 0 && button < mouseButtons.length)
            return mouseButtons[button];
        return false;
    }

    public Vec2 getMousePosition()
    {
        return new Vec2(mouseX, mouseY);
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public int getMouseDragDeltaX() {
        return mouseDragDeltaX;
    }

    public int getMouseDragDeltaY() {
        return mouseDragDeltaY;
    }

    public void resetMouseDrag()
    {
        mouseDragDeltaX = 0;
        mouseDragDeltaY = 0;
    }


    @Override
    public void keyTyped(KeyEvent e) {
        // todo
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        onKeyEvent.handle(e);
        if (key >= 0 && key < pressedKeys.length) {
            pressedKeys[key] = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key >= 0 && key < pressedKeys.length) {
            pressedKeys[key] = false;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        onMouseClicked.handle(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        onMouseClicked.handle(e);
        int button = e.getButton();
        if (button >= 0 && button < mouseButtons.length) {
            mouseButtons[button] = true;
            lastMouseX = e.getX();
            lastMouseY = e.getY();
            draggingButton = button;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int button = e.getButton();
        if (button >= 0 && button < mouseButtons.length) {
            mouseButtons[button] = false;
            mouseButtonDragging[button] = false;
            mouseDragDeltaX = 0;
            mouseDragDeltaY = 0;
            if (draggingButton == button) {
                draggingButton = -1;
            }
        }
        onMouseReleased.handle(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // todo
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // todo
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (draggingButton != -1) {
            mouseButtonDragging[draggingButton] = true;
        }
        mouseX = e.getX();
        mouseY = e.getY();
        mouseDragDeltaX = mouseX - lastMouseX;
        mouseDragDeltaY = mouseY - lastMouseY;
        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    public int getScrollDeltaX() {
        return scrollDeltaX;
    }

    public int getScrollDeltaY() {
        return scrollDeltaY;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        // Update lastMouseX and lastMouseY even if the mouse is not moved
        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    public void resetScrollDelta() {
        scrollDeltaX = 0;
        scrollDeltaY = 0;
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        scrollDeltaX = e.getWheelRotation() * e.getScrollAmount();
        scrollDeltaY = e.getUnitsToScroll() * e.getScrollAmount();
    }
}

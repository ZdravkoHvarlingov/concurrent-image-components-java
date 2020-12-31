import components.ComponentsDrawer;


public class Boot {

    public static void main(String[] args) {
        nu.pattern.OpenCV.loadShared();

        ComponentsDrawer componentsDrawer = new ComponentsDrawer();
        componentsDrawer.drawAndSave("images/landscape.jpg", 16);
    }
}

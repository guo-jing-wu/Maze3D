package maze3d;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import java.awt.Dimension;
import java.awt.Toolkit;

public class Main extends SimpleApplication {

    static Dimension screen;
    BitmapText text;
    int count = 0;
    float tpfSum;
    Node sNode, cameraTarget;
    Material mat, mat1, floor_mat, wall_mat;
    Geometry geomLarge, geomSmall, geomGround;
    MazeCell cell;
    Maze maze;

    public static void main(String[] args) {
        Main app = new Main();
        initAppScreen(app);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        initGui();
        initMaterial();
        initLightandShadow();
        initCam();
        //
        buildMaze(40, 30);
    }

    // -------------------------------------------------------------------------
    private void buildMaze(int cols, int rows) {
        floor(rows, cols);
        maze = new Maze(rows, cols, false);
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                cell = new MazeCell(wall_mat, maze, r, c);
                cell.setLocalTranslation(-(rows - 1f), 0f, -cols);
                cell.setShadowMode(ShadowMode.CastAndReceive);
                this.rootNode.attachChild(cell);
            }
        }
        Box sbox = new Box(0.125f, 0.5f, cols);
        Geometry swall = new Geometry("Box", sbox);
        swall.setMaterial(wall_mat);
        sbox.scaleTextureCoordinates(new Vector2f(cols, 1));
        swall.setLocalTranslation(new Vector3f(rows, 0.5f, 0f));
        swall.setShadowMode(ShadowMode.CastAndReceive);
        this.rootNode.attachChild(swall);

        Box ebox = new Box(0.125f, 0.5f, rows);
        Geometry ewall = new Geometry("Box", ebox);
        ewall.setMaterial(wall_mat);
        ebox.scaleTextureCoordinates(new Vector2f(rows, 1));
        Quaternion pitch90 = new Quaternion();
        pitch90.fromAngleAxis(FastMath.PI / 2, new Vector3f(0f, 1f, 0f));
        ewall.setLocalRotation(pitch90);
        ewall.setLocalTranslation(new Vector3f(0, 0.5f, cols));
        ewall.setShadowMode(ShadowMode.CastAndReceive);
        this.rootNode.attachChild(ewall);
    }

    @Override
    public void simpleUpdate(float tpf) {
        cameraTarget.rotate(0f, tpf, 0f);
    }

    // -------------------------------------------------------------------------
    // Inits
    // -------------------------------------------------------------------------
    private static void initAppScreen(SimpleApplication app) {
        AppSettings aps = new AppSettings(true);
        screen = Toolkit.getDefaultToolkit().getScreenSize();
        screen.width *= 0.75;
        screen.height *= 0.75;
        aps.setResolution(screen.width, screen.height);
        app.setSettings(aps);
        app.setShowSettings(false);
    }

    private void initMaterial() {
        mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Ambient", ColorRGBA.Red);
        mat.setColor("Diffuse", ColorRGBA.Green);
        mat.setColor("Specular", ColorRGBA.White);
        mat.setFloat("Shininess", 12f); // shininess from 1-128

        mat1 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat1.setBoolean("UseMaterialColors", true);
        mat1.setColor("Ambient", new ColorRGBA(0.5f, 0.25f, 0.05f, 1.0f));
        mat1.setColor("Diffuse", new ColorRGBA(0.5f, 0.25f, 0.05f, 1.0f));
        mat1.setColor("Specular", ColorRGBA.Gray);
        mat1.setFloat("Shininess", 2f); // shininess from 1-128

        floor_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture floor = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
        floor_mat.setTexture("ColorMap", floor);

        wall_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture wall = assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg");
        wall.setWrap(Texture.WrapMode.Repeat);
        wall_mat.setTexture("ColorMap", wall);
    }

    private void initGui() {
        setDisplayFps(true);
        setDisplayStatView(false);
    }

    private void initLightandShadow() {
        // Light1: white, directional
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.7f, 0.9f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);

        // Light2: white, directional
        DirectionalLight sun2 = new DirectionalLight();
        sun2.setDirection((new Vector3f(0.5f, -0.7f, -0.9f)).normalizeLocal());
        sun2.setColor(ColorRGBA.White);
        rootNode.addLight(sun2);

        // Light 3: Ambient, gray
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(new ColorRGBA(0.3f, 0.3f, 0.3f, 1.0f));
        rootNode.addLight(ambient);

        //Shadow: directional
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 4096, 2);
        dlsr.setLight(sun);
        viewPort.addProcessor(dlsr);

        //Shadow: ambient
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        SSAOFilter ssao = new SSAOFilter(12.94f, 43.92f, 0.33f, 0.61f);
        fpp.addFilter(ssao);
        viewPort.addProcessor(fpp);
    }

    private void initCam() {
        flyCam.setEnabled(false);
        cameraTarget = new Node();
        CameraNode camNode = new CameraNode("Camera Node", cam);
        camNode.setLocalTranslation(new Vector3f(40f, 40f, 0f));
        camNode.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        cameraTarget.attachChild(camNode);
        rootNode.attachChild(cameraTarget);
    }

    public void floor(float x, float z) {
        Box box = new Box(x, 0.1f, z);
        geomGround = new Geometry("Floor", box);
        geomGround.setMaterial(floor_mat);
        geomGround.setLocalTranslation(0, -0.1f, 0);
        geomGround.setShadowMode(ShadowMode.Receive);
        this.rootNode.attachChild(geomGround);
    }
}

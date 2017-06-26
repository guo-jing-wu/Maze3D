/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maze3d;

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

public class MazeCell extends Node {

    static Box box = new Box(0.125f, 0.5f, 1.0f);

    protected MazeCell(Material wall_mat, Maze maze, int row, int col) {
        if (maze.hasNorthWall(row, col)) {
            Geometry wall = new Geometry("Box", box);
            wall.setMaterial(wall_mat);
            wall.setLocalTranslation(new Vector3f(-1 + row * 2, 0.5f, 1 + col * 2));
            attachChild(wall);
        }
        if (maze.hasWestWall(row, col)) {
            Geometry wall = new Geometry("Box", box);
            wall.setMaterial(wall_mat);
            Quaternion pitch90 = new Quaternion();
            pitch90.fromAngleAxis(FastMath.PI / 2, new Vector3f(0f, 1f, 0f));
            wall.setLocalRotation(pitch90);
            wall.setLocalTranslation(new Vector3f(row * 2, 0.5f, col * 2));
            attachChild(wall);
        }
    }
}
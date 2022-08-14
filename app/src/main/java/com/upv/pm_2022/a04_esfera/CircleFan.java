package com.upv.pm_2022.a04_esfera;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static android.opengl.GLES20.*;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * This class renders a triangle fan;
 */
public class CircleFan implements GLSurfaceView.Renderer {
    private int mProgram; // Pointer to the program
    private int positionHandle; // Pointer to the vPosition variable in the vertex shader
    private int colorHandle; // Pointer to the vColor variable in the fragment shader

    private int vertexStride = COORDS_PER_VERTEX * 4; // Size of the stride (4 because of floats)
    private String vertexShader =
            "attribute vec4 vPosition;\n" +
            "void main () {\n" +
            // TODO: Multiply positions by matrix to resize the figure given the height and width
            "   gl_Position = vPosition;\n" +
            "}\n";
    private String fragmentShader =
            "precision lowp float;\n" + //In order to do faster rendering we set low float precision
            "uniform vec4 vColor;\n" +
            "void main() {\n" +
                // TODO: Change this to color for every vertex
//                "  gl_FragColor = vColor;\n" +
                "  gl_FragColor = vec4(1.0, 0.5, 0.0, 1.0);" +
            "}\n";
    private FloatBuffer vertexBuffer; // Buffer to use
    private static final int COORDS_PER_VERTEX = 2; // We only store 2 coords per vertex

    public int sides; // How many sides does the polygon will have

    /**
     * Set the default size of the figure and allocate memory for the buffer and data
     */
    public CircleFan(int sides) {
        this.sides = (sides > 3 && sides < 360) ? sides : 3; // Set default polygon type
        calculateData();
    }

    /**
     * Compile and link the shaders
     */
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // Set background color
        mProgram = glCreateProgram(); // Tell OpenGL to create a new program and return the id

        // Load shaders source code and attach to the created program
        int vs = loadShader(GL_VERTEX_SHADER, vertexShader);
        int fs = loadShader(GL_FRAGMENT_SHADER, fragmentShader);
        glAttachShader(mProgram, vs);
        glAttachShader(mProgram, fs);

        // Link the shaders in the created program
        glLinkProgram(mProgram);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        glViewport(0, 0, width, height);
    }

    /**
     * Recalculate the data, that is the vertex positions and colors.
     * <p>
     * Stores the data into the OpenGL buffer
     * <p>
     * This method should be call everytime the sides attribute is changed
     */
    public void calculateData() {
        // Calculate the vertexes
        float[] positions = drawCircle(0.0f, 0.0f, 0.5f, sides);
        // Populate buffer with calculated data
        vertexBuffer = ByteBuffer.allocateDirect(positions.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(positions).position(0);

        int buffers[] = new int[1];
        glGenBuffers(1, buffers, 0); // Generate one buffer
        glBindBuffer(GL_ARRAY_BUFFER, buffers[0]); // Use this buffer
        // Tell openGL the size of this buffer, that we will use it dynamically and store it
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer.capacity()*4, vertexBuffer, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    /**
     * Draw calculated polygon
     * @param gl10
     */
    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT); // Clear screen to redraw
        // TODO: Some of this functionality shouldn't be here
        // Use the created program
        glUseProgram(mProgram);

        // Get the pointer to the vPosition variable
        positionHandle = glGetAttribLocation(mProgram, "vPosition");

        // Enable writing into vertex attributes
        glEnableVertexAttribArray(positionHandle);
        // Set the attributes of a vertex
        glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GL_FLOAT,
                           false, vertexStride, vertexBuffer);

//        // Get the pointer to the vColor variable
//        colorHandle = glGetUniformLocation(mProgram, "vColor");
//        // Set color of the vertex
//        glUniform4fv(colorHandle, 1, color, 0);

        // Draw the whole thing
        glDrawArrays(GL_TRIANGLES, 0, (sides*5)/COORDS_PER_VERTEX);
        glDisableVertexAttribArray(positionHandle);
    }

    // This class is only for reference of what does a vertex stores
    private class Vertex {
        private float[] position; // 2 coordinates
        private float[] color; // 3 attributes (RGB)
    }

    public void increaseSides() {
        if(this.sides < 360) {
            this.sides++; calculateData(); //System.out.println(sides);
        }
    }

    public void decreaseSides() {
        if(this.sides > 3) {
            this.sides--; calculateData(); //System.out.println(sides);
        }
    }

    /**
     * Load the given source (This is a helper function)
     * @param type type of the source (vertex or fragment)
     * @param source source code
     * @return returns the id of the loaded shader
     */
    public static int loadShader(int type, String source) {
        int id = glCreateShader(type); // Tell OpenGL to create a new shader and return its id
        glShaderSource(id, source); // Add source code to the created shader
        glCompileShader(id); // Compile shader
        return id;
    }

    /**
     * Calculate a circle in a triangle fan fashion way
     * @param x center of the circle in the x-axis
     * @param y center of the circle in the x-axis
     * @param r radius of the circle
     * @param sides how many sides the circle should have
     * @return returns an array of vertexes to draw
     * TODO: Add coloring
     */
    public float[] drawCircle(float x, float y, float r, int sides) {
        // Each side is a triangle, a triangle is composed of 3 vertex
        int nVertices = 3*sides; //

        float[] verticesX = new float[nVertices];
        float[] verticesY = new float[nVertices];

        // Set center of the circle
//        verticesX[0] = x;
//        verticesY[0] = y;

        // Calculate the vertex to draw
        int counter = 1;
        for(int i = 0; i < nVertices; i++) {
            if(i % 3 == 0) { // If it is a multiple of 3 then set the center of the polygon
                verticesX[i] = x;
                verticesY[i] = y;
            } else {
                verticesX[i] = x + r * (float) Math.cos(counter * 2 * Math.PI / sides);
                verticesY[i] = y + r * (float) Math.sin(counter * 2 * Math.PI / sides);
                counter++;
            }
        }

        // OUTPUT = {1,1,2,2,3,3}
        // DESIRED = {0,0,1,1,

        // Store all the vertexes in a single array
        float[] allVertices = new float[2 * nVertices];
        for(int i = 0; i < nVertices; i++) {
            allVertices[i * 2]      = verticesX[i];
            allVertices[i * 2 + 1]  = verticesY[i];
        }
        for(int i = 0; i < allVertices.length; i=i+2) {
            System.out.println( "Coord " + (i/2) + ": " + allVertices[i] + ", " + allVertices[i+1]);
        }
        return allVertices;
    }
}

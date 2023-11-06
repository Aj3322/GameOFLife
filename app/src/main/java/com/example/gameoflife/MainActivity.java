package com.example.gameoflife;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private int rows = 10;
    private int cols = 10;
    private int[][] grid = new int[rows][cols];
    private TextView[][] textViews = new TextView[rows][cols];
    private GridLayout gridLayout;
    private boolean isRunning = false;
    private int liveCount; // Counter for live cells
    private int deadCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Game OF Life");
        getSupportActionBar().setSubtitle("Created By Aj_Styles");

        gridLayout = findViewById(R.id.gridLayout);

        initializeGrid();
        initializeTextViews();
        displayGrid();
        // Display counts of live and dead cells
        displayCounts();

        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);
        Button restartButton = findViewById(R.id.restartButton);

        // Inside your MainActivity
        Button githubButton = findViewById(R.id.gitHub); // Find your button by its ID
        githubButton.setOnClickListener(view -> {
            String githubUrl = "https://github.com/yourUsername/yourRepository"; // Replace with your GitHub URL
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl));
            startActivity(intent);
        });


        startButton.setOnClickListener(v -> {
            startSimulation();
            startButton.setBackgroundColor(getResources().getColor(R.color.clickedColor));
            stopButton.setBackgroundColor(getResources().getColor(R.color.button));
            restartButton.setBackgroundColor(getResources().getColor(R.color.button));
        });

        stopButton.setOnClickListener(v -> {
            stopSimulation();
            stopButton.setBackgroundColor(getResources().getColor(R.color.clickedColor));
            startButton.setBackgroundColor(getResources().getColor(R.color.button));
            restartButton.setBackgroundColor(getResources().getColor(R.color.button));
        });

        restartButton.setOnClickListener(v -> {
            restartSimulation();
            restartButton.setBackgroundColor(getResources().getColor(R.color.clickedColor));
            startButton.setBackgroundColor(getResources().getColor(R.color.button));
            stopButton.setBackgroundColor(getResources().getColor(R.color.button));
        });
    }
    private void displayCounts() {
        int liveCount = countLiveCells(); // Method to count live cells
        int deadCount = countDeadCells(); // Method to count dead cells

        TextView liveCountTextView = findViewById(R.id.liveCountTextView);
        TextView deadCountTextView = findViewById(R.id.deadCountTextView);

        liveCountTextView.setText("Live: " + liveCount);
        deadCountTextView.setText("Dead: " + deadCount);

        liveCountTextView.setTextColor(getResources().getColor(R.color.liveColor)); // Set color for live count text
        deadCountTextView.setTextColor(getResources().getColor(R.color.deadColor)); // Set color for dead count text

        liveCountTextView.setTypeface(null, Typeface.BOLD); // Set font style for live count text
        deadCountTextView.setTypeface(null, Typeface.BOLD); // Set font style for dead count text
    }
    private int countLiveCells() {
        int liveCount = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 1) {
                    liveCount++;
                }
            }
        }
        return liveCount;
    }

    private int countDeadCells() {
        int deadCount = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 0) {
                    deadCount++;
                }
            }
        }
        return deadCount;
    }
    private void initializeGrid() {
        Random rand = new Random();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // Adjust the probability to balance live and dead cells
                if (rand.nextInt(100) < 50) {
                    grid[i][j] = 1; // Set as live cell
                } else {
                    grid[i][j] = 0; // Set as dead cell
                }
            }
        }
    }

    private void initializeTextViews() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                TextView textView = new TextView(this);
                textView.setWidth(80);
                textView.setHeight(80);
                textView.setPadding(5, 5, 5, 5);
                textViews[i][j] = textView;
                gridLayout.addView(textView);
            }
        }
    }


    private void displayGrid() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
//                int liveNeighbors = countNeighbors(i, j); // Count live neighbors
//                textViews[i][j].setText(String.valueOf(liveNeighbors)); // Display live neighbor count
                if (grid[i][j] == 1) {
                    textViews[i][j].setBackgroundColor(getResources().getColor(R.color.black));
                } else {
                    textViews[i][j].setBackgroundColor(getResources().getColor(R.color.white));
                }
            }
        }
    }

    private void startSimulation() {
        if (!isRunning) {
            isRunning = true;
            simulate();
        }
    }

    private void stopSimulation() {
        isRunning = false;
    }

    private void restartSimulation() {
        stopSimulation(); // Stop simulation logic
        initializeGrid(); // Reinitialize the grid
        liveCount = 0; // Reset live cell count
        deadCount = 0; // Reset dead cell count
        displayCounts(); // Update the count TextViews with reset counts
        startSimulation(); // Start simulation logic again
    }
    private void simulate() {
        new Thread(() -> {
            while (true) {
                grid = simulateGeneration();
                runOnUiThread(() -> {
                    displayGrid();
                    displayCounts(); // Update live and dead cell counts continuously
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    private int[][] simulateGeneration() {
        int[][] nextGrid = new int[rows][cols];
        int currentLiveCount = countLiveCells();

        if (currentLiveCount < 10) {
            // Reproduction logic - randomly spawn live cells
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (grid[i][j] == 0) {
                        // Chance to spawn a live cell if it's currently dead
                        if (new Random().nextInt(100) < 20) { // Adjust probability as needed (e.g., 20 = 20% chance)
                            grid[i][j] = 1; // Set the cell as alive
                        }
                    }
                }
            }
        }
        if (countDeadCells() > 60) {
            // Reproduction logic - randomly spawn live cells
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (grid[i][j] == 0) {
                        // Chance to spawn a live cell if it's currently dead
                        if (new Random().nextInt(100) < 20) { // Adjust probability as needed (e.g., 20 = 20% chance)
                            grid[i][j] = 1; // Set the cell as alive
                        }
                    }
                }
            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int neighbors = countNeighbors(i, j);
                if (grid[i][j] == 1) {
                    if (neighbors < 2 || neighbors > 3) {
                        nextGrid[i][j] = 0;
                    } else {
                        nextGrid[i][j] = 1;
                    }
                } else {
                    if (neighbors == 3) {
                        nextGrid[i][j] = 1;
                    } else {
                        nextGrid[i][j] = 0;
                    }
                }
            }
        }
        return nextGrid;
    }

    private int countNeighbors(int row, int col) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int n_row = row + i;
                int n_col = col + j;
                if (i == 0 && j == 0) continue;
                if (n_row >= 0 && n_row < rows && n_col >= 0 && n_col < cols) {
                    count += grid[n_row][n_col];
                }
            }
        }
        return count;
    }


}

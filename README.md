# WiFiCoverage: A Fast and Accurate Tool for Automatic Indoor Coverage Analysis

This project was developed using the **Kotlin** programming language, with the goal of analysing the signal coverage in indoor environments. It receives the input data from files in **JSON** format (see `src/main/kotlin/main/*.json` for examples). The input files specify parameters of the propagation model, the details of the area to be analyzed (*e.g.*, area, divisions), and a list of APs. The output is a report in TXT format which lists the estimated signal attenuation from each AP to points spread throughout the area of analysis. The resolution of those points can be controlled from the input **JSON** file.

We also provide an auxiliary tool that plots heatmaps based on the report. Optionally, the heatmaps can be plotted over a floor plant of the area to be analyzed.

To view the algorithms used in the application, you can access the source code available in this repository.

## Technologies Used

- Kotlin
- GSON (for JSON parsing)
- IntelliJ IDEA (optional, only for development)
- Python3, matplotlib, pandas, PIL, numpy (for the heatmap generation)
- Terminal (for compiling and running without IntelliJ)

## Running the Program via Terminal

We provide a precompiled `jar` in the `build/libs` directory. From that directory, simply run:

```bash
java -jar projeto-1.0-SNAPSHOT.jar <path_to_input.json>
```

The report file should be generated on the same directory.

If you wish to plot the heatmaps, you can use the `heatmap.py` application found on the `tools` directory. To use it, simply run:

```bash
python3 heatmap.py -r <path_to_report_file>
```

If you have a figure with the floor plan of the area being analyzed, you can use it as a background for the heatmaps:

```bash
python3 heatmap.py -p <path_to_ground_plant> -r <path_to_report_file>
```

This tool has a few more options available. You can check them out in the help:

```bash
python3 heatmap.py -h
```

   

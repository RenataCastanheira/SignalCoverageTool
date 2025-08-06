# Project documentation
## Domain
### File: areaValidations
#### isValidSubRegion(buildingArea: AREA, subRegionsAreas: List<DENSITY>)
This function aims to validate whether the data provided by the user is valid, namely:
* check if the subregion has a valid size;
* check if the subregion is out of the area’s bounds.
In conclusion, the function returns true if the data is valid, and false otherwise.


#### areTheAreasOverlapping(buildingArea: AREA, subRegionsAreas:List<DENSITY>)
This function aims to verify if the areas given by the user are overlapping.

    REQUIREMENT: to run this function, we must ensure that the function isValidSubRegion() returns true.

As we check whether a region is valid, we add it to the list (subRegionsValidated). We created this variable to compare 
a new area with the areas that have already been validated.

With the variable ***counter***, we can identify which area from the provided list we are currently working with. This 
way, if any area fails validation, we print this value before adding the subregion to the list of validated subregions, 
so the user knows which area needs to be changed

As we go through each subregion and each validated subregion:
* we check for overlap using the function *isOverlapping()*. If it returns false, it means the subregion does not 
overlap with any validated subregion, and therefore it is added to the list of validated subregions otherwise, it 
immediately returns true, ending the function.
* the counter is also incremented to inform the user that the area at index x is valid.

#### isOverlapping(subRegion: DENSITY, subRegionValidated: DENSITY)
This function aims to perform all the necessary checks to determine whether the region in question overlaps with any of 
the validated regions.

To ensure that this region does not overlap with any validated subregion, we checked whether the:
* subregion is inside another one
* subregion is completely contained within another 
* (...) COMPLETAR -- IFS DESCOMENTADOS

#### doTheSubRegionsCoverAllArea(buildingArea: AREA, subRegions:List<DENSITY>)

    REQUIREMENT: to run this function, we must ensure that the functions:
    * isValidSubRegion() returns true
    * areTheAreasOverlapping() returns false

The logic of this function is to calculate the area of each subregion. If the total area is equal to the area of the
entire region, it returns true; otherwise, it returns false.

#### checkAdjacencyBetweenTwoRegions(region1: DENSITY, region2: DENSITY): Boolean

     REQUIREMENT: to run this function, we must ensure that the functions:
    * isValidSubRegion() returns true
    * areTheAreasOverlapping() returns false
    * doTheSubRegionsCoverAllArea() returns true

The logic of this function is based on calling the function ***getContactWallEndpoints()***. If this function throws an 
exception, it returns false; otherwise, if ***getContactWallEndpoints()*** returns a wall, it returns true.

#### getAdjacentRegionIndexes(region: DENSITY, allRegions: List<DENSITY>)

     REQUIREMENT: to run this function, we must ensure that the functions:
    * isValidSubRegion() returns true
    * areTheAreasOverlapping() returns false
    * doTheSubRegionsCoverAllArea() returns true
    * checkAdjacencyBetweenTwoRegions() returns true

This function aims to determine the start and end points of the contact wall between two adjacent subregions. The 
contact wall can be positioned either vertically (between side walls) or horizontally (between top/bottom walls).

**Function Logic:**

1.	**Extracting coordinates and dimensions:**
The function begins by retrieving the positions (x, y) and dimensions (width, height) of each provided subregion 
(wall1 and wall2).

2. **Checking for vertical adjacency (side walls):**
* It checks whether the right side of one subregion is aligned with the left side of the other (or vice versa).
* If they are vertically adjacent, the intersection along the vertical axis (y) between the two subregions is 
calculated.
* If there is a valid overlap (i.e., startY < endY), the function returns a pair of coordinates representing the 
vertical contact wall.

3. **Checking for horizontal adjacency (top/bottom walls):**
* It checks whether the bottom side of one subregion is aligned with the top side of the other (or vice versa).
* If they are horizontally adjacent, the intersection along the horizontal axis (x) between the two subregions is 
calculated.
* If there is an overlap (startX < endX), the function returns a pair of coordinates representing the horizontal 
contact wall.

4. **If no adjacency is found:**
* If none of the above conditions are met, the function throws an exception, indicating that the subregions are not 
adjacent and therefore no valid contact wall exists.

#### wallMidpoint(wall1: DENSITY, wall2: DENSITY)

This function calculates the midpoint of the contact wall between two adjacent subregions.
It calls the helper function **_getContactWallEndpoints()_**, which returns the wall endpoints, and then returns the 
midpoint.

    REQUIREMENT: to run this function, we must ensure that the functions:
    * isValidSubRegion() returns true
    * areTheAreasOverlapping() returns false
    * doTheSubRegionsCoverAllArea() returns true
    * checkAdjacencyBetweenTwoRegions() returns true

#### whichRegionDoesThePointBelong (point:COORDINATES, subRegionsAreas: List<DENSITY>)

This function returns the index of the subregion in the list of subregions.


### File: regionStructureUtils
#### buildPropagationTree(
    current: Int,
    destiny: Int,
    allSubregions: List<DENSITY>,
    visited: Set<Int> = emptySet(),
    parent: Int? = null,
    parentTotalDist: Double = 0.0,
    apOrigin: COORDINATES,
    targetPoint: COORDINATES
)

This function builds a propagation tree representing all possible paths from a starting subregion (`current`) to a
destination subregion (`destiny`). It recursively explores all unvisited adjacent regions, building a tree structure
that reflects the connectivity and spatial distances between subregions.

The function uses adjacency information stored in each `DENSITY` object to determine the next regions to explore.
It also calculates:
- The distance from the parent region to the current region using midpoints of shared walls.
- The cumulative distance from the access point (AP) to each visited region's wall.
- The final distance from the destination region’s wall to a specific target point (e.g., user location or exit).

Each `Node` in the tree contains:
- The region index.
- A list of children nodes representing the next steps.
- The distance from the previous region.
- The total cumulative distance from the origin AP.

If no adjacent regions remain unvisited or no valid path leads to the destination, a `Leaf` is returned.

#### REQUIREMENTS
Before calling this function, ensure:
* `isValidSubRegion()` returns `true` for all elements in `allSubregions`.
* `areTheAreasOverlapping()` returns `false` for all pairs of subregions.
* `doTheSubRegionsCoverAllArea()` returns `true`.
* Each `DENSITY` object has its `adjacencyRegionsIndexes` correctly populated.
* The access point (`apOrigin`) belongs to a valid region in `allSubregions`.
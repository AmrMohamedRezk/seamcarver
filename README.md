seamcarver
==========

SeamCarver

Seam-carving is a content-aware image resizing technique where the image is reduced in size by one pixel of height (or width) at a time. A vertical seam in an image is a path of pixels connected from the top to the bottom with one pixel in each row.

Finding and removing a seam involves three parts and a tiny bit of notation:

Notation. In image processing, pixel (x, y) refers to the pixel in column x and row y, with pixel (0, 0) at the upper left corner and pixel (W − 1, H − 1) at the bottom right corner. This is the opposite of the standard mathematical notation used in linear algebra where (i, j) refers to row i and column j and with Cartesian coordinates where (0, 0) is at the lower left corner.


Steps :

1-Energy calculation. The first step is to calculate the energy of each pixel, which is a measure of the importance of each pixel—the higher the energy, the less likely that the pixel will be included as part of a seam (as we'll see in the next step). 
The energy is high (white) for pixels in the image where there is a rapid color gradient . The seam-carving technique avoids removing such high-energy pixels.

2-Seam identification. The next step is to find a vertical seam of minimum total energy. This is similar to the classic shortest path problem in an edge-weighted digraph except for the following:
  -The weights are on the vertices instead of the edges.
  -We want to find the shortest path from any of W pixels in the top row to any of the W pixels in the bottom row.
  -The digraph is acyclic, where there is a downward edge from pixel (x, y) to pixels (x − 1, y + 1), (x, y + 1), and (x +     1, y + 1), assuming that the coordinates are in the prescribed range.

3-Seam removal. The final step is remove from the image all of the pixels along the seam.

source : http://www.cs.princeton.edu/courses/archive/spring13/cos226/assignments/seamCarving.html

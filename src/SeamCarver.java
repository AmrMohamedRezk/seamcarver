import java.awt.Color;
import java.io.File;

public class SeamCarver {
	Picture picture;
	double energy[][];

	public SeamCarver(Picture picture) {
		this.picture = picture;
	}

	private void buildGraphHorizontal(EdgeWeightedDigraph g) {
		int source = 0;
		for (int i = 0; i < width(); i++)
			g.addEdge(new DirectedEdge(source, i + 1, 195075));
		for (int i = 0; i < height() - 1; i++) {
			for (int j = 0; j < width(); j++) {
				int pixelX = j;
				int pixelY = i;
				int from = convert(pixelX, pixelY);
				if (isValid(pixelX - 1, pixelY + 1)) {
					int to = convert(pixelX - 1, pixelY + 1);
					g.addEdge(new DirectedEdge(from, to, energy(pixelX - 1,
							pixelY + 1)));
				}
				if (isValid(pixelX, pixelY + 1)) {
					int to = convert(pixelX, pixelY + 1);
					g.addEdge(new DirectedEdge(from, to, energy(pixelX,
							pixelY + 1)));
				}
				if (isValid(pixelX + 1, pixelY + 1)) {
					int to = convert(pixelX + 1, pixelY + 1);
					g.addEdge(new DirectedEdge(from, to, energy(pixelX + 1,
							pixelY + 1)));
				}
			}
		}
		int destination = width() * height() + 1;
		for (int i = destination - 1, j = 0; j < width(); j++, i--)
			g.addEdge(new DirectedEdge(i, destination, 0));

	}

	private void buildGraphVertical(EdgeWeightedDigraph g) {
		int source = 0;
		for (int i = 0; i < height(); i++)
			g.addEdge(new DirectedEdge(source, i * width() + 1, 195075));
		for (int i = 0; i < height(); i++) {
			for (int j = 0; j < width(); j++) {
				int pixelX = j;
				int pixelY = i;
				int from = convert(pixelX, pixelY);

				if (isValid(pixelX + 1, pixelY - 1)) {
					int to = convert(pixelX + 1, pixelY - 1);
					g.addEdge(new DirectedEdge(from, to, energy(pixelX + 1,
							pixelY - 1)));
				}
				if (isValid(pixelX + 1, pixelY)) {
					int to = convert(pixelX + 1, pixelY);
					g.addEdge(new DirectedEdge(from, to, energy(pixelX + 1,
							pixelY)));
				}
				if (isValid(pixelX + 1, pixelY + 1)) {
					int to = convert(pixelX + 1, pixelY + 1);
					g.addEdge(new DirectedEdge(from, to, energy(pixelX + 1,
							pixelY + 1)));
				}
			}
		}
		int destination = width() * height() + 1;
		for (int j = 0; j < height(); j++) {
			int from = j * width() + width();
			g.addEdge(new DirectedEdge(from, destination, 0));
		}

	}

	private int convert(int x, int y) {
		return y * width() + x + 1;
	}

	public Picture picture() {
		// current picture
		return picture;
	}

	public int width() {
		// width of current picture
		return picture.width();
	}

	public int height() {
		// height of current picture
		return picture.height();
	}

	private boolean isValid(int x, int y) {
		return x < width() && y < height() && x >= 0 && y >= 0;
	}

	public double energy(int x, int y) {
		if (isValid(x - 1, y) && isValid(x + 1, y) && isValid(x, y - 1)
				&& isValid(x, y + 1)) {
			Color left = picture.get(x - 1, y);
			Color right = picture.get(x + 1, y);
			Color top = picture.get(x, y - 1);
			Color bottom = picture.get(x, y + 1);

			double rx = Math.abs(left.getRed() - right.getRed());
			double gx = Math.abs(left.getGreen() - right.getGreen());
			double bx = Math.abs(left.getBlue() - right.getBlue());

			double ry = Math.abs(top.getRed() - bottom.getRed());
			double gy = Math.abs(top.getGreen() - bottom.getGreen());
			double by = Math.abs(top.getBlue() - bottom.getBlue());

			return Math.pow(rx, 2) + Math.pow(gx, 2) + Math.pow(bx, 2)
					+ Math.pow(ry, 2) + Math.pow(gy, 2) + Math.pow(by, 2);

		}
		return 195075;
	}

	private void relax(DirectedEdge e, DirectedEdge[] edgeTo, double[] distTo) {
		int v = e.from(), w = e.to();
		if (distTo[w] > distTo[v] + e.weight()) {
			distTo[w] = distTo[v] + e.weight();
			edgeTo[w] = e;
		}
	}

	public int[] findVerticalSeam() {
		EdgeWeightedDigraph g = new EdgeWeightedDigraph(height() * width() + 2);
		buildGraphHorizontal(g);
		DirectedEdge[] edgeTo = new DirectedEdge[g.V()];
		double[] distTo = new double[g.V()];
		for (int v = 0; v < g.V(); v++)
			distTo[v] = Double.POSITIVE_INFINITY;
		distTo[0] = 0.0;

		Topological topological = new Topological(g);
		for (int v : topological.order())
			for (DirectedEdge e : g.adj(v))
				relax(e, edgeTo, distTo);
		int destination = g.V() - 1;
		int least[] = new int[height()];
		for (int i = 0; i < height(); i++) {
			DirectedEdge current = edgeTo[destination];
			least[height() - (i + 1)] = (current.from() - 1) % width();
			destination = current.from();
		}
		return least;

	}

	DirectedEdge[] edgeTo;
	double[] distTo;
	private IndexMinPQ<Double> pq;

	private void relax2(DirectedEdge e) {
		int v = e.from(), w = e.to();
		if (distTo[w] > distTo[v] + e.weight()) {
			distTo[w] = distTo[v] + e.weight();
			edgeTo[w] = e;
			if (pq.contains(w))
				pq.decreaseKey(w, distTo[w]);
			else
				pq.insert(w, distTo[w]);
		}
	}

	public int[] findHorizontalSeam2() {
		EdgeWeightedDigraph g = new EdgeWeightedDigraph(height() * width() + 2);
		buildGraphVertical(g);
		edgeTo = new DirectedEdge[g.V()];
		distTo = new double[g.V()];
		pq = new IndexMinPQ<Double>(g.V());
		for (int v = 0; v < g.V(); v++)
			distTo[v] = Double.POSITIVE_INFINITY;
		distTo[0] = 0.0;
		pq.insert(0, 0.0);
		while (!pq.isEmpty()) {
			int v = pq.delMin();
			for (DirectedEdge e : g.adj(v))
				relax2(e);
		}

		/*
		 * Topological topological = new Topological(g); for (int v :
		 * topological.order()) for (DirectedEdge e : g.adj(v)) relax(e);
		 */
		// dijkstraSP(g, 0);
		int destination = g.V() - 1;
		int least[] = new int[width()];
		for (int i = 0; i < width(); i++) {
			DirectedEdge current = edgeTo[destination];
			least[width() - (i + 1)] = (current.from() - 1) / width();
			destination = current.from();
		}
		return least;

	}

	public int[] findHorizontalSeam() {
		EdgeWeightedDigraph g = new EdgeWeightedDigraph(height() * width() + 2);
		buildGraphVertical(g);
		DirectedEdge[] edgeTo = new DirectedEdge[g.V()];
		double[] distTo = new double[g.V()];
		for (int v = 0; v < g.V(); v++)
			distTo[v] = Double.POSITIVE_INFINITY;
		distTo[0] = 0.0;

		Topological topological = new Topological(g);
		for (int v : topological.order())
			for (DirectedEdge e : g.adj(v))
				relax(e, edgeTo, distTo);
		int destination = g.V() - 1;
		int least[] = new int[width()];
		for (int i = 0; i < width(); i++) {
			DirectedEdge current = edgeTo[destination];
			least[width() - (i + 1)] = (current.from() - 1) / width();
			destination = current.from();
		}
		return least;

	}

	public void removeHorizontalSeam(int[] a) {
		// remove horizontal seam from current picture
		printHorizontalSeam();
		Picture newPic = new Picture(picture.width(), picture.height()-1);
		int x=0;
		for (int j = 0; j < picture.width(); j++) {
			for (int i = 0; i < picture.height(); i++) {
				if (a[j] != i)
					newPic.set(j,x++, picture.get(j, i));
			}
			x=0;
		}
		picture = newPic;
		printHorizontalSeam();
	}

	public void removeVerticalSeam(int[] a) {
		printVerticalSeam();
		Picture newPic = new Picture(picture.width() - 1, picture.height());
		int x = 0;
		for (int i = 0; i < picture.height(); i++) {
			for (int j = 0; j < picture.width(); j++) {
				if (a[i] != j)
					newPic.set(x++, i, picture.get(j, i));
			}
			x = 0;
		}

		picture = newPic;
		printVerticalSeam();

	}

	private void printVerticalSeam() {

		for (int j = 0; j < height(); j++) {
			for (int i = 0; i < width(); i++) {
				char lMarker = ' ';
				char rMarker = ' ';

				System.out.printf("%c%6.0f%c ", lMarker, energy(i, j), rMarker);
			}

			System.out.println();
		}

	}

    private  void printHorizontalSeam()
    {        

        for (int j = 0; j < height(); j++)
        {
            for (int i = 0; i <width(); i++)            
            {
                char lMarker = ' ';
                char rMarker = ' ';

                System.out.printf("%c%6.0f%c ", lMarker, energy(i, j), rMarker);
            }
            System.out.println();
        }                
        
    }
	public static void main(String[] args) {
		Picture t = new Picture("HJocean.png");
		SeamCarver s = new SeamCarver(t);
		int[] least = s.findHorizontalSeam();
		s.removeHorizontalSeam(least);
		s.picture.save(new File("amr.png"));
	}
}

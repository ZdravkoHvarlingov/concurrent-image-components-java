# Image connected components - concurrent algorithm
##### Java implementation made by Zdravko Hvarlingov

## Content
1. What are connected components? Why are they useful in image processing?
2. Task definition and mathematical model
3. Solution key ideas
4. Java implementation and usages
5. Test results
6. Result examples
7. Conclusion

## Connected components and their usefulness
In terms of graph theory finding the connected components is a relatively old task for both directed and undirected graphs.
Later that same problem is found in the field of image processing.
If we have to define it intuitively, we could say that a connected component of pixels is an image area where all the pixels have a similar color.
 
Usually finding the components of an image is a pre-processing step. The result is afterwards used as an input for further processing.
A quite simple application could be counting the number of elements inside the image (the number of coins for example :moneybag:)

## Task definition and mathematical model
Let's first define the task we are trying to solve. As stated above we are trying to find the image areas with similar color value.
This can be described in another way as we are trying to find the connected component for every single pixel.
Every pixel should have one even if it is the only one inside, right :sunglasses:?

The task can be easily transformed into a graph theory problem. Let's say we have a graph _**G <V, E>**_.
_**V**_ is the set of nodes and in our case every pixel will be a node inside the graph.
_**E**_ is the set of edges, **_<pixel1, pixel>_** is an edge only if **_pixel1_** and **_pixel2_** have similar colors and they are adjacent pixels.
If we imagine the image as a rectangular matrix of pixels, by adjacent we mean that both pixels share common side or edge. So using this definition every pixel has 8 adjacent pixels.

Using the above mathematical model the final task is to find the number of connected components inside that graph in a concurrent fashion :rocket:.

#### Jar download link
```
https://drive.google.com/file/d/1p6CVfiscnOg8ZwGw6Fd5R0S1HRzvftYq/view?usp=sharing
```

#### Usage
```
java -jar executable.jar -i ../images/landscape.jpg -t 10 -s 7 --verbose
```

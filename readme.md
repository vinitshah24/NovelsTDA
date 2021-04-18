## BookCoReference.java
* Reads all the books from "./novels_mannually_cleaned" and writes co-reference results in "./raw_index".
* List of the books can be found in "_List.of.Novels.csv". 
* Please download and put in the "./novels_mannually_cleaned" folder. 
* For the best same results, manually remove the meta data and indexes.

## indices_to_graphs.r
* Transforms the books in "raw_index" dir to the graph of characters in "./wasserstein" dir.

## graphs_to_persistence_diagram_classification.r
* Use the graphs in "./wasserstein" dir to generate persistence diagrams and save the classification results in "./ph_results".

## draw_persistence_diagrams.r
* Draws persistent diagrams in the folder "./diagrams".
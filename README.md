# Information Retrieval Project 1

The aim of this work is to create a Apache Lucene search engine.

The Cranfield Collection, which is a collection of ~1400 documents, is indexed by the code.

- Can be found at http://ir.dcs.gla.ac.uk/resources/test_collections/cran/
- To unzip the download tar -xzf cran.tar.gz

To evaluate how well the search algorithm works, trec eval
software
- wget https://trec.nist.gov/trec_eval/trec_eval-9.0.7.tar.gz
- Extract the files: tar -xzf trec_eval-9.0.9.tar.gz
  - Look for usage: in README for installation – very straightforward – make
  - Inside trec_eval-9.0.7 enter “make”
  - Then test the installation using “make quicktest”
  
The search engine is then tested using 225 queries, with different scoring approaches.

To run the code, use the following command

```
sh run_files.sh
```

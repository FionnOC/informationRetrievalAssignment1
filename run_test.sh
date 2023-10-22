echo "Running some tests ..."
mvn package
echo ""

echo "Removing existing index ..."
rm -r index/
echo ""

echo "English Analyser, VSM ..."
java -jar target/informationRetrieval-1.0-SNAPSHOT.jar

echo "Running trec_eval on the results..."
./trec_eval-9.0.7/trec_eval corpus/QRelsCorrectedforTRECeval results/query_results.txt
#
#echo "Removing existing index ..."
#rm -r index/
#echo ""

#echo "English Analyser, VSM ..."
#java -jar target/informationRetrieval-1.0-SNAPSHOT.jar standard
#
#echo "Running trec_eval on the results..."
#./trec_eval-9.0.7/trec_eval corpus/QRelsCorrectedforTRECeval results/query_results.txt

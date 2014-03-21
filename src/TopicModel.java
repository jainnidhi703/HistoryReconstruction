//package cc.mallet.examples;

import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelSequence;

import java.util.*;

public class TopicModel {

    public List<Cluster> getClusters(List<XmlDocument> documentList, int numTopics) throws Exception {

        MalletDataImporter importer = new MalletDataImporter(MalletDataImporter.PipeType.Array);

//        InstanceList instances = importer.readDirectory(new File(dirName));
        InstanceList instances = importer.readXmlDocuments(documentList);

        // Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
        //  Note that the first parameter is passed as the sum over topics, while
        //  the second is

        double alpha_t = 0.01;
        double alpha_sum = alpha_t * numTopics;
        ParallelTopicModel model = new ParallelTopicModel(numTopics, alpha_sum, 0.01);

        model.addInstances(instances);

        // Use two parallel samplers, which each look at one half the corpus and combine
        //  statistics after every iteration.
        model.setNumThreads(4);

        // FIXME : Run the model for 50 iterations and stop (this is for testing only,
        //  for real applications, use 1000 to 2000 iterations)
        model.setNumIterations(50);
        model.estimate();

//        File file = new File("topics.txt");
//        model.printDocumentTopics(file);

        HashMap<String, Document> docMap = new HashMap<String, Document>();
        for(XmlDocument xmlDoc : documentList) {
            Document d = new Document(-1,xmlDoc.getFilename(),xmlDoc.getTitle() + " " + xmlDoc.getContent());
            docMap.put(xmlDoc.getFilename(), d);
        }

        numTopics = model.getNumTopics();
        int[] topicCounts = new int[ numTopics ];

        List<Cluster> clusters = new ArrayList<Cluster>(numTopics);
        for(int i = 0; i < numTopics; ++i) {
            Cluster c = new Cluster(i, null);
            clusters.add(i, c);
        }

        for(int doc = 0; doc < model.data.size(); ++doc) {
            LabelSequence topicSequence = model.data.get(doc).topicSequence;
            int[] currentDocTopics = topicSequence.getFeatures();

            int docLen = currentDocTopics.length;

            // Count up the tokens
            for (int currentDocTopic : currentDocTopics) {
                topicCounts[currentDocTopic]++;
            }

            int mxTopic = -1;
            double mxWeight = -1.0;

            // And normalize
            for (int topic = 0; topic < numTopics; topic++) {
                double tmp = (model.alpha[topic] + topicCounts[topic]) / (docLen + model.alphaSum);
                if(tmp > mxWeight) {
                    mxTopic = topic;
                    mxWeight = tmp;
                }
            }

            Document dd = docMap.get(model.data.get(doc).instance.getName().toString());
            dd.setClusterID(mxTopic);
            clusters.get(mxTopic).addDocument(dd);

            Arrays.fill(topicCounts, 0);
        }

        return clusters;
    }

}
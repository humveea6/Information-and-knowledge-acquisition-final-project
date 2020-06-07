package information_grep.backend;

import java.util.*;

/**
 * @author LingChen <lingchen@kuaishou.com>
 * Created on 2020-06-06
 */
public class BM25 {
    /**
     * 文档句子的个数
     */
    int D;

    /**
     * 文档句子的平均长度
     */
    double avgdl;

    /**
     * 拆分为[句子[单词]]形式的文档
     */
    List<List<String>> docs;

    /**
     * 文档全文
     */
    List<String> completeDocs;

    /**
     * 文档标题
     */
    List<String> titles;

    /**
     * 文档中每个句子中的每个词与词频
     */
    Map<String, Integer>[] f;

    /**
     * 文档中全部词语与出现在几个句子中
     */
    Map<String, Integer> df;

    /**
     * IDF
     */
    Map<String, Double> idf;

    /**
     * 调节因子
     */
    final static float k1 = 1.5f;

    /**
     * 调节因子
     */
    final static float b = 0.75f;

    public BM25(List<List<String>> docs, List<String> completeDocs, List<String> titles) {
        this.docs = docs;
        this.completeDocs = completeDocs;
        this.titles = titles;
        D = docs.size();
        for (List<String> sentence : docs) {
            avgdl += sentence.size();
        }
        avgdl /= D;
        f = new Map[D];
        df = new TreeMap<String, Integer>();
        idf = new TreeMap<String, Double>();
        init();
    }

    /**
     * 在构造时初始化自己的所有参数
     */
    private void init() {
        int index = 0;
        for (List<String> sentence : docs) {
            Map<String, Integer> tf = new TreeMap<String, Integer>();
            for (String word : sentence) {
                Integer freq = tf.get(word);
                freq = (freq == null ? 0 : freq) + 1;
                tf.put(word, freq);
            }
            f[index] = tf;
            for (Map.Entry<String, Integer> entry : tf.entrySet()) {
                String word = entry.getKey();
                Integer freq = df.get(word);
                freq = (freq == null ? 0 : freq) + 1;
                df.put(word, freq);
            }
            ++index;
        }
        for (Map.Entry<String, Integer> entry : df.entrySet()) {
            String word = entry.getKey();
            Integer freq = entry.getValue();
            idf.put(word, Math.log(D - freq + 0.5) - Math.log(freq + 0.5));
        }
    }

    /**
     * 计算一个句子与一个文档的BM25相似度
     *
     * @param sentence 句子（查询语句）
     * @param index    文档（用语料库中的下标表示）
     * @return BM25 score
     */
    public BM25Scores sim(List<String> sentence, int index) {
        double score = 0;
        for (String word : sentence) {
            if (!f[index].containsKey(word)) continue;
            int d = docs.get(index).size();
            Integer tf = f[index].get(word);
            score += (idf.get(word) * tf * (k1 + 1)
                    / (tf + k1 * (1 - b + b * d
                    / avgdl)));
        }

        return new BM25Scores(score,index);
    }

    public List<BM25Scores> simAll(List<String> sentence) {
        List<BM25Scores> bm25ScoresList = new ArrayList<>();
        for (int i = 0; i < D; ++i) {
            bm25ScoresList.add(sim(sentence, i));
        }

        Collections.sort(bm25ScoresList, new Comparator<BM25Scores>() {
            @Override
            public int compare(BM25Scores o1, BM25Scores o2) {
                if(o1.getScore()>o2.getScore()){
                    return -1;
                }
                else if(o2.getScore()>o1.getScore()){
                    return 1;
                }
                else{
                    return 0;
                }
            }
        });

        return bm25ScoresList;
    }
}



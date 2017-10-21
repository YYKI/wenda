package com.nowcoder.service;


import com.nowcoder.model.Question;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    private static final String SORL_URL = "http://127.0.0.1:8983/solr/wenda";
    private HttpSolrClient client = new HttpSolrClient.Builder(SORL_URL).build();
    private static final String QUESTION_TITLE_FIELD = "question_title";
    private static final String QUESTION_CONTENT_FIELD = "question_content";

    public List<Question> searchQuestion(String keyword, int offset, int limit,
                                         String hlPre, String hlPos) throws Exception{
        List<Question> questionList = new ArrayList<>();
        SolrQuery query = new SolrQuery(keyword);
        query.setRows(limit);
        query.setStart(offset);
        query.setHighlight(true);
        query.setHighlightSimplePost(hlPos);
        query.setHighlightSimplePre(hlPre);
        query.set("hl.fl", QUESTION_TITLE_FIELD+","+QUESTION_CONTENT_FIELD);
        QueryResponse response = client.query(query);
        for(Map.Entry<String, Map<String, List<String>>> entry : response.getHighlighting().entrySet()){
            Question question = new Question();
            question.setId(Integer.parseInt(entry.getKey()));
            if(entry.getValue().containsKey(QUESTION_CONTENT_FIELD)){
                List<String> contentList = entry.getValue().get(QUESTION_CONTENT_FIELD);
                if(contentList.size()>0)
                    question.setContent(contentList.get(0));
            }
            if(entry.getValue().containsKey(QUESTION_TITLE_FIELD)){
                List<String> titleList = entry.getValue().get(QUESTION_TITLE_FIELD);
                if(titleList.size()>0)
                    question.setTitle(titleList.get(0));
            }

            questionList.add(question);
        }

        return questionList;
    }

    public boolean indexQuestion(int qid, String title, String content) throws Exception{
        SolrInputDocument doc = new SolrInputDocument();
        doc.setField("id", qid);
        doc.setField(QUESTION_TITLE_FIELD, title);
        doc.setField(QUESTION_CONTENT_FIELD, content);
        UpdateResponse response = client.add(doc,1000);
        return response!=null && response.getStatus()==0;

    }

}

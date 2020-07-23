package net.zhuyongqi.forum.service.impl;

import net.zhuyongqi.forum.dao.CategoryDao;
import net.zhuyongqi.forum.dao.ReplyDao;
import net.zhuyongqi.forum.domain.Category;
import net.zhuyongqi.forum.domain.Reply;
import net.zhuyongqi.forum.domain.Topic;
import net.zhuyongqi.forum.domain.User;
import net.zhuyongqi.forum.dto.PageDTO;
import net.zhuyongqi.forum.dao.TopicDao;
import net.zhuyongqi.forum.service.TopicService;

import java.util.Date;
import java.util.List;

public class TopicServiceImpl implements TopicService {

    private TopicDao topicDao=new TopicDao();
    private ReplyDao replyDao=new ReplyDao();
    private CategoryDao categoryDao=new CategoryDao();
    @Override
    public PageDTO<Topic> listTopicPageByCid(int cId, int page, int pageSize) {

        //查询总记录数
        int totalRecordNum=topicDao.countTotalTopicByCid(cId);

        int from=(page-1)*pageSize;

        //分页查询
        List<Topic> topicList =topicDao.findListByCid(cId,from,pageSize);

        PageDTO<Topic> pageDTO=new PageDTO<>(page,pageSize,totalRecordNum);

        pageDTO.setList(topicList);

        return pageDTO;
    }

    @Override
    public Topic findById(int topicId) {
        return topicDao.findById(topicId);
    }

    @Override
    public PageDTO<Reply> findReplyPageByTopicId(int topicId, int page, int pageSize) {

        //查询总的回复
        int totalRecordNum=replyDao.countTotalReplyByCid(topicId);
        int from=(page-1)*pageSize;

        //分页查询
        List<Reply> replyList =topicDao.findListByTopicId(topicId,from,pageSize);

        PageDTO<Reply> pageDTO=new PageDTO<>(page,pageSize,totalRecordNum);

        pageDTO.setList(replyList);
        return pageDTO;
    }

    @Override
    public int addTopic(User loginUser, String title, String content, int cId) {
        Category category=categoryDao.findById(cId);
        if(category==null)return 0;
        Topic topic=new Topic();
        topic.setTitle(title);
        topic.setContent(content);
        topic.setCreateTime(new Date());
        topic.setPv(1);
        topic.setDelete(0);
        topic.setUserId(loginUser.getId());
        topic.setUsername(loginUser.getUsername());
        topic.setUserImg(loginUser.getImg());
        topic.setcId(cId);
        topic.setHot(0);

        int rows= 0;
        try {
            rows = topicDao.save(topic);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }

    @Override
    public int replyByTopicId(User loginUser, int topicId, String content) {

        int floor=topicDao.findLatestFloorByTopicId(topicId);
        Reply reply=new Reply();
        reply.setContent(content);
        reply.setCreateTime(new Date());
        reply.setUpdateTime(new Date());
        reply.setFloor(floor+1);
        reply.setTopicId(topicId);
        reply.setUserId(loginUser.getId());
        reply.setUsername(loginUser.getUsername());
        reply.setImg(loginUser.getImg());
        reply.setDelete(0);

        int rows=replyDao.save(reply);
        return rows;
    }

    @Override
    public void addOnePV(int topicId) {

        Topic topic=topicDao.findById(topicId);
        int newPV=topic.getPv()+1;
        topicDao.updatePV(topicId,newPV,topic.getPv());
    }
}

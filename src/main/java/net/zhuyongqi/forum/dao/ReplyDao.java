package net.zhuyongqi.forum.dao;

import net.zhuyongqi.forum.domain.Category;
import net.zhuyongqi.forum.domain.Reply;
import net.zhuyongqi.forum.util.DataSourceUtil;
import org.apache.commons.dbutils.*;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.List;

public class ReplyDao {
    private QueryRunner queryRunner=new QueryRunner(DataSourceUtil.getDataSource());

    //开启驼峰映射
    private BeanProcessor beanProcessor=new GenerousBeanProcessor();
    private RowProcessor processor=new BasicRowProcessor(beanProcessor);

    /**
     * 根据topic_id查询回复总记录
     * @param topicId
     * @return
     */
    public int countTotalReplyByCid(int topicId) {
        String sql="select count(*) from reply where topic_id=?";
        Long count=null;
        try {
            count=(Long) queryRunner.query(sql,new ScalarHandler<>(),topicId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return count.intValue();
    }

    /**
     * 插入新的回复
     * @param reply
     * @return
     */
    public int save(Reply reply) {
        String sql="insert into reply (topic_id,floor,content,user_id,username,user_img,create_time,update_time,`delete`)"+
                "values (?,?,?,?,?,?,?,?,?)";

        Object [] params={
                reply.getTopicId(),
                reply.getFloor(),
                reply.getContent(),
                reply.getUserId(),
                reply.getUsername(),
                reply.getImg(),
                reply.getCreateTime(),
                reply.getUpdateTime(),
                reply.getDelete()
        };

        int rows=0;
        try {
            rows=queryRunner.update(sql,params);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rows;
    }
}

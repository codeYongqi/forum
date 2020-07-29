package net.zhuyongqi.forum.controller;

import net.zhuyongqi.forum.domain.Reply;
import net.zhuyongqi.forum.domain.Topic;
import net.zhuyongqi.forum.domain.User;
import net.zhuyongqi.forum.dto.PageDTO;
import net.zhuyongqi.forum.service.CategoryService;
import net.zhuyongqi.forum.service.TopicService;
import net.zhuyongqi.forum.service.impl.CategoryServiceImpl;
import net.zhuyongqi.forum.service.impl.TopicServiceImpl;
import org.omg.CORBA.PUBLIC_MEMBER;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "topicServlet" ,urlPatterns = {"/topic"})
public class TopicServlet extends BaseServlet{

    private TopicService topicService=new TopicServiceImpl();
    private CategoryService categoryService=new CategoryServiceImpl();

    //默认分页大小
    private static final int pageSize=5;

    public void list(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int cId=Integer.parseInt(request.getParameter("c_id"));

        //默认第一页
        int page=1;

        String currentPage=request.getParameter("page");
        if(currentPage!=null&&currentPage!=""){
            page=Integer.parseInt(currentPage);
        }

        PageDTO<Topic> pageDTO=topicService.listTopicPageByCid(cId,page,pageSize);

        System.out.println(pageDTO.toString());

        request.getSession().setAttribute("categoryList",categoryService.list());
        request.setAttribute("topicPage",pageDTO);
        request.getRequestDispatcher("/index.jsp").forward(request,response);
    }

    /**
     * 查看主题的全部回复
     * @param request
     * @param response
     */
    public void findDetailById(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {

        //获取topicId
        int topicId=Integer.parseInt(request.getParameter("topic_id"));
        //默认第一页
        int page=1;

        String currentPage=request.getParameter("page");
        if(currentPage!=null&&currentPage!=""){
            page=Integer.parseInt(currentPage);
        }

        //处理浏览量，如果同个session内只算一次
        String sessionReadKey="is_read_"+topicId;
        Boolean isRead=(Boolean) request.getSession().getAttribute(sessionReadKey);
        if(isRead==null){
            request.getSession().setAttribute(sessionReadKey,true);

            //新增一个pv
            topicService.addOnePV(topicId);
        }

        Topic topic=topicService.findById(topicId);
        PageDTO<Reply> pageDTO=topicService.findReplyPageByTopicId(topicId,page,pageSize);


        System.out.println(pageDTO.toString());

        request.setAttribute("topic",topic);
        request.setAttribute("replyPage",pageDTO);

        request.getRequestDispatcher("/topic_detail.jsp").forward(request,response);
    }

    /**
     * 发布主题
     * @param request
     * @param response
     */
    public void addTopic(HttpServletRequest request,HttpServletResponse response) throws IOException {
        User loginUser =(User) request.getSession().getAttribute("loginUser");
        if(loginUser==null){
            request.setAttribute("msg","请登录");
            //页面跳转 TODO
            response.sendRedirect("/user/login.jsp");
        }
        String title=request.getParameter("title");
        String content=request.getParameter("content");
        int cId=Integer.parseInt(request.getParameter("c_id"));

        topicService.addTopic(loginUser,title,content,cId);

        //发布主题成功
        response.sendRedirect("/topic?method=list&c_id="+cId);

    }

    /**
     * 盖楼回复
     * @param request
     * @param response
     */
    public void replyByTopicId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User loginUser =(User) request.getSession().getAttribute("loginUser");
        if(loginUser==null){
            request.setAttribute("msg","请登录");
            //页面跳转 TODO
            response.sendRedirect("/user/login.jsp");
            return;
        }

        int topicId=Integer.parseInt(request.getParameter("topic_id"));
        String content=request.getParameter("content");

        int rows=topicService.replyByTopicId(loginUser,topicId,content);

        response.sendRedirect("/topic?method=findDetailById&topic_id="+topicId+"&page=1");
    }
}

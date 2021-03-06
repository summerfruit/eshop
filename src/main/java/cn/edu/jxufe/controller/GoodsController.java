package cn.edu.jxufe.controller;

import cn.edu.jxufe.entity.GoodsComment;
import cn.edu.jxufe.entity.Goodsinfo;
import cn.edu.jxufe.entity.Memberinfo;
import cn.edu.jxufe.entity.Searchinfo;
import cn.edu.jxufe.service.CommentService;
import cn.edu.jxufe.service.GoodsInfoService;
import cn.edu.jxufe.service.MemberInfoService;
import cn.edu.jxufe.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RequestMapping(value = "/goods")
@Controller
public class GoodsController {
    @Autowired
    private GoodsInfoService goodsInfoService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private SearchService searchService;
    @Autowired
    private MemberInfoService memberInfoService;

    @RequestMapping(value = "info{gid}")
    public String getGoodsInfo(@PathVariable("gid") int gid, ModelMap map,HttpSession session){
        System.out.println("---------------------------------"+gid);
        Goodsinfo goods = goodsInfoService.findByGoodsId(gid);
        if(goods==null&&goods.getGoodsState()==0){
            return "404";
        }
        map.put("author",memberInfoService.findNameByMid(goods.getMemberId()));
        map.put("goods",goods);
        session.setAttribute("gid",gid);
        return "product";
    }

    @RequestMapping(value = "next_page")
    @ResponseBody
    public Object getNextPage(@RequestParam(name = "page") int page,@RequestParam(name = "count",defaultValue = "10") int count){
        return goodsInfoService.findByPage(page,count);
    }

    //search表查询
    @RequestMapping(value = "infobygoodsname")
    public Object getGoodsInfoByGname(String gname,ModelMap map,HttpSession session){
        System.out.println("输入的商品名称是"+gname);
        Memberinfo user = (Memberinfo) session.getAttribute("user");

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        java.util.Date time=null;
        try {
            time= df.parse(df.format(new Date())); //将刚得到的时间字符串转换为Date类型
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(user!=null) {
            searchService.insertJiLu(gname,time,user.getMemberId());//插入搜索记录
            List<Searchinfo> slist = searchService.showKey(user.getMemberId()); //显示历史记录
            map.put("slist", slist);
        }
        List<Goodsinfo> glist=goodsInfoService.findGoodsByGname(gname);
        if(glist.isEmpty()){
            return "404";
        }else {
            map.put("glist",glist);
            return "search";
        }
    }



    //发布评论
    @RequestMapping(value = "uploadcom")
    public Object saveComment(String com, HttpSession session, ModelMap map){
        System.out.println("输入的评论内容是"+com);
        // System.out.println(df.format(new Date()));// new Date()为获取当前系统时间;
        Memberinfo user = (Memberinfo) session.getAttribute("user");
        if(user!=null) {
            Integer goodsid = (Integer) session.getAttribute("gid");
            if (goodsid != null) {
                commentService.insertComment(goodsid, com,user);
                goodsInfoService.commentPlus(goodsid);
                List<GoodsComment> clist = commentService.showContent(goodsid);
                map.put("clist", clist);
            }
        }
        return "comment";
    }

    @RequestMapping(value = "mywork")
    public String myWorks(HttpSession session,ModelMap map){
        Memberinfo user = (Memberinfo) session.getAttribute("user");
        if(user!=null){
            map.put("gls",goodsInfoService.findByAuthor(user.getMemberId()));
        }
        return "product_list";
    }
}

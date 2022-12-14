package com.project.bilibili.api;

import com.project.bilibili.api.support.UserSupport;
import com.project.bilibili.dao.VideoDao;
import com.project.bilibili.domain.*;
import com.project.bilibili.exception.ConditionException;
import com.project.bilibili.service.ElasticSearchService;
import com.project.bilibili.service.VideoService;
import org.apache.mahout.cf.taste.common.TasteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
public class VideoApi {
    @Autowired
    private VideoService videoService;

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @PostMapping("/videos")
    public JsonResponse<String> addVideos(@RequestBody Video video)
    {
        Long userId = userSupport.getCurrentUserId();
        video.setUserId(userId);
        videoService.addVideo(video);
        elasticSearchService.addVideo(video);
        return JsonResponse.success();
    }

    @PostMapping("/delete-videos-es")
    public JsonResponse<String> deleteVideoEs()
    {
        elasticSearchService.deleteAllVideos();
        return JsonResponse.success();
    }

    @GetMapping("/videos")
    public JsonResponse<PageResult<Video>> pageListVideos(Integer size,Integer no,String area)
    {
        PageResult<Video> result = videoService.pageListVideos(size, no, area);
        return new JsonResponse<>(result);
    }

//  es查询视频
    @GetMapping("/es-videos")
    public JsonResponse<Video> pageEsVideos(@RequestParam String keyword)
    {
        Video result = elasticSearchService.getVideos(keyword);
        return new JsonResponse<>(result);
    }

//  使用http输出流的方式进行输出，所以不用返回值具体数据
    @GetMapping("/video-slices")
    public void viewViderOnlineBySlices(HttpServletRequest request, HttpServletResponse response,String url) throws Exception {
        videoService.viewViderOnlineBySlices(request,response,url);
    }

    /**
     * 点赞视频
     */
    @PostMapping("/video-likes")
    public JsonResponse<String> addVideoLike(@RequestParam Long videoId)
    {
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoLike(videoId,userId);
        return JsonResponse.success();
    }

    /**
     * 取消视频点赞
     */
    @DeleteMapping("/video-likes")
    public JsonResponse<String> deleteVideoLike(@RequestParam Long videoId)
    {
        Long userId = userSupport.getCurrentUserId();
        videoService.deleteVideoLike(videoId,userId);
        return JsonResponse.success();
    }

    /**
     * 查询视频点赞数量
     */
    @GetMapping("/video-likes")
    public JsonResponse<Map<String,Object>> getVideoLikes(@RequestParam Long videoId)
    {
//      由于不登陆也可以查看点赞数量，因此可以userId=null,抛出异常后忽视即可
        Long userId = null;
        try {
            userId = userSupport.getCurrentUserId();
        }catch (Exception ignore){}
        Map<String,Object> result = videoService.getVideoLikes(videoId,userId);
        return new JsonResponse<>(result);
    }

    /**
     * 收藏视频
     */
    @PostMapping("/video-collections")
    public JsonResponse<String> addVideoCollection(@RequestParam VideoCollection videoCollection)
    {
        Long userId = userSupport.getCurrentUserId();
        videoCollection.setUserId(userId);
        videoService.addVideoCollection(videoCollection,userId);
        return JsonResponse.success();
    }

    /**
     * 取消视频收藏
     * 仅涉及到找到这个视频的收藏这一条信息，只要video的id即可
     */
    @DeleteMapping("/video-collections")
    public JsonResponse<String> deleteVideoCollection(@RequestParam Long videoId)
    {
        Long userId = userSupport.getCurrentUserId();
        videoService.deleteVideoCollection(userId,videoId);
        return JsonResponse.success();
    }

    /**
     * 查询视频收藏数量
     */
    @GetMapping("/video-collections")
    public JsonResponse<Map<String,Object>> getVideoCollection(@RequestParam Long videoId)
    {
        Long userId = null;
        try {
            userId = userSupport.getCurrentUserId();
        }catch (Exception ignore){}
        Map<String, Object> videoCollection = videoService.getVideoCollection(videoId, userId);
        return new JsonResponse<>(videoCollection);
    }

    /**
     * 视频投币
     */
    @PostMapping("/video-coins")
    public JsonResponse<String> addVideoCoins(@RequestBody VideoCoin videoCoin)
    {
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoCoins(videoCoin,userId);
        return JsonResponse.success();
    }

    /**
     * 添加视频评论
     */
    @PostMapping("/video-comments")
    public JsonResponse<String> addVideoComment(@RequestBody VideoComment videoComment)
    {
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoComment(userId,videoComment);
        return JsonResponse.success();
    }

    /**
     * 分页查询视频评论
     */
    @GetMapping("/video-comments")
    public JsonResponse<PageResult<VideoComment>> pageListVideoComments(@RequestParam Integer size,@RequestParam Integer no,@RequestParam Long videoId)
    {
        PageResult<VideoComment> result = videoService.getVideoComments(size,no,videoId);
        return new JsonResponse<>(result);
    }

    /**
     * 获取视频详情
     */
    @GetMapping("/video-detail")
    public JsonResponse<Map<String,Object>> getVideoDetails(@RequestParam Long videoId)
    {
//      因为要返回视频详情和用户信息(要展示)
        Map<String,Object> result = videoService.getVideoDetails(videoId);
        return new JsonResponse<>(result);
    }

    /**
     * 添加视频观看记录
     * request用于获取用户相关情况
     * 区分游客：操作系统+浏览器(userAgent)+IP
     */
    @PostMapping("/video-views")
    public JsonResponse<String> addVideoView(@RequestBody VideoView videoView,HttpServletRequest request)
    {
        Long userId;
        try {
            userId = userSupport.getCurrentUserId();
            videoView.setUserId(userId);
            videoService.addVideoView(videoView,request);
        }catch (Exception ignore)
        {
            videoService.addVideoView(videoView,request);
        }
        return JsonResponse.success();
    }

    /**
     * 获取视频播放量
     */
    @GetMapping("/video-view-counts")
    public JsonResponse<Integer> getVideoViewCounts(@RequestParam Long videoId)
    {
        Integer count = videoService.getVideoViewCounts(videoId);
        return new JsonResponse<>(count);
    }

    /**
     * 视频内容推荐
     */
    @GetMapping("/recommendations")
    public JsonResponse<List<Video>> recommend() throws TasteException {
        Long userId;
        List<Video> list;
        userId = userSupport.getCurrentUserId();
        list = videoService.recommend(userId);
//        try {
//
//        }catch (Exception e)
//        {
//            throw new ConditionException("参数错误！");
//        }
        return new JsonResponse<>(list);
    }



}

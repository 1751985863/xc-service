package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-09-12 18:32
 **/
@Service
public class PageService {

    @Autowired
    CmsPageRepository cmsPageRepository;


    /**
     * 页面查询方法
     * @param page 页码，从1开始记数
     * @param size 每页记录数
     * @param queryPageRequest 查询条件
     * @return
     */
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest){
        if (queryPageRequest==null){
            queryPageRequest=new QueryPageRequest();
        }


        ExampleMatcher exampleMatcher=ExampleMatcher.matching()
                .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        CmsPage cmsPage=new CmsPage();
        if (StringUtils.isNoneEmpty(queryPageRequest.getSiteId())){
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }

        if (StringUtils.isNoneEmpty(queryPageRequest.getTemplateId())){
            cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        }
        if (StringUtils.isNoneEmpty(queryPageRequest.getPageAliase())){
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        Example<CmsPage> example=Example.of(cmsPage,exampleMatcher);

        //分页参数
        if(page <=0){
            page = 1;
        }
        page = page -1;
        if(size<=0){
            size = 10;
        }
        Pageable pageable = PageRequest.of(page,size);
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);
        QueryResult queryResult = new QueryResult();
        queryResult.setList(all.getContent());//数据列表
        queryResult.setTotal(all.getTotalElements());//数据总记录数
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }
    public CmsPageResult add(CmsPage cmsPage){
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (cmsPage1!=null){
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }


        cmsPage.setPageId(null);
        cmsPageRepository.save(cmsPage);
        CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, cmsPage);
        return cmsPageResult;
    }

    public CmsPage getById(String id){
        Optional<CmsPage> optional=cmsPageRepository.findById(id);
        if (!optional.isPresent()){
            ExceptionCast.cast(CommonCode.FAIL);
        }
        CmsPage cmsPage=optional.get();
        return cmsPage;
    }

    public CmsPageResult update(String id,CmsPage cmsPage){

        CmsPage cmsPage1 = this.getById(id);
        if (cmsPage!=null){
             cmsPage1.setPageAliase(cmsPage.getPageAliase());
             cmsPage1.setPageId(cmsPage.getPageId());
             cmsPage1.setTemplateId(cmsPage.getTemplateId());
             cmsPage1.setSiteId(cmsPage.getSiteId());
             cmsPage1.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
             cmsPage1.setPageWebPath(cmsPage.getPageWebPath());
             cmsPageRepository.save(cmsPage1);
             return new CmsPageResult(CommonCode.SUCCESS,cmsPage1);
        }
        return new CmsPageResult(CommonCode.FAIL,null);

    }

    public ResponseResult delete(String id){
        Optional<CmsPage> optional=cmsPageRepository.findById(id);
        if (optional.isPresent()){
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);

    }
}

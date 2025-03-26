package org.jeecg.modules.learntime.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.learntime.entity.FileStorage;
import org.jeecg.modules.learntime.service.IFileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
* @Description: file_storage
* @Author: jeecg-boot
* @Date:   2022-03-10
* @Version: V1.0
*/
@Api(tags="file_storage")
@RestController
@RequestMapping("/learntime/fileStorage")
@Slf4j
public class FileStorageController extends JeecgController<FileStorage, IFileStorageService> {
   @Autowired
   private IFileStorageService fileStorageService;

   /**
    * 分页列表查询
    *
    * @param fileStorage
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   @AutoLog(value = "file_storage-分页列表查询")
   @ApiOperation(value="file_storage-分页列表查询", notes="file_storage-分页列表查询")
   @GetMapping(value = "/list")
   public Result<?> queryPageList(FileStorage fileStorage,
                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                  HttpServletRequest req) {
       QueryWrapper<FileStorage> queryWrapper = QueryGenerator.initQueryWrapper(fileStorage, req.getParameterMap());
       Page<FileStorage> page = new Page<FileStorage>(pageNo, pageSize);
       IPage<FileStorage> pageList = fileStorageService.page(page, queryWrapper);
       return Result.OK(pageList);
   }

   /**
    *   添加
    *
    * @param fileStorage
    * @return
    */
   @AutoLog(value = "file_storage-添加")
   @ApiOperation(value="file_storage-添加", notes="file_storage-添加")
   @PostMapping(value = "/add")
   public Result<?> add(@RequestBody FileStorage fileStorage) {
       fileStorageService.save(fileStorage);
       return Result.OK("添加成功！");
   }

   /**
    *  编辑
    *
    * @param fileStorage
    * @return
    */
   @AutoLog(value = "file_storage-编辑")
   @ApiOperation(value="file_storage-编辑", notes="file_storage-编辑")
   @PutMapping(value = "/edit")
   public Result<?> edit(@RequestBody FileStorage fileStorage) {
       fileStorageService.updateById(fileStorage);
       return Result.OK("编辑成功!");
   }

   /**
    *   通过id删除
    *
    * @param id
    * @return
    */
   @AutoLog(value = "file_storage-通过id删除")
   @ApiOperation(value="file_storage-通过id删除", notes="file_storage-通过id删除")
   @DeleteMapping(value = "/delete")
   public Result<?> delete(@RequestParam(name="id",required=true) String id) {
       fileStorageService.removeById(id);
       return Result.OK("删除成功!");
   }

   /**
    *  批量删除
    *
    * @param ids
    * @return
    */
   @AutoLog(value = "file_storage-批量删除")
   @ApiOperation(value="file_storage-批量删除", notes="file_storage-批量删除")
   @DeleteMapping(value = "/deleteBatch")
   public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       this.fileStorageService.removeByIds(Arrays.asList(ids.split(",")));
       return Result.OK("批量删除成功!");
   }

   /**
    * 通过id查询
    *
    * @param id
    * @return
    */
   @AutoLog(value = "file_storage-通过id查询")
   @ApiOperation(value="file_storage-通过id查询", notes="file_storage-通过id查询")
   @GetMapping(value = "/queryById")
   public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
       FileStorage fileStorage = fileStorageService.getById(id);
       if(fileStorage==null) {
           return Result.error("未找到对应数据");
       }
       return Result.OK(fileStorage);
   }

   /**
   * 导出excel
   *
   * @param request
   * @param fileStorage
   */
   @RequestMapping(value = "/exportXls")
   public ModelAndView exportXls(HttpServletRequest request, FileStorage fileStorage) {
       return super.exportXls(request, fileStorage, FileStorage.class, "file_storage");
   }

   /**
     * 通过excel导入数据
   *
   * @param request
   * @param response
   * @return
   */
   @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
   public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
       return super.importExcel(request, response, FileStorage.class);
   }

}

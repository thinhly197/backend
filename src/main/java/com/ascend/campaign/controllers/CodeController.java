package com.ascend.campaign.controllers;

import com.ascend.campaign.constants.Errors;
import com.ascend.campaign.constants.Response;
import com.ascend.campaign.entities.CodeDetail;
import com.ascend.campaign.exceptions.CodeNotFoundException;
import com.ascend.campaign.exceptions.CodeTypeException;
import com.ascend.campaign.models.CodeGeneratorRequest;
import com.ascend.campaign.models.ResponseModel;
import com.ascend.campaign.services.CodeGeneratorService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class CodeController {
    @NonNull
    CodeGeneratorService codeGeneratorService;

    @Autowired
    public CodeController(CodeGeneratorService codeGeneratorService) {
        this.codeGeneratorService = codeGeneratorService;
    }

    @RequestMapping(value = "/codegroups", method = RequestMethod.POST,
            headers = {"Content-type=application/json"})
    public HttpEntity<ResponseModel> generateCode(@RequestBody CodeGeneratorRequest codeGeneratorRequest) {
        CodeDetail codeDetail = codeGeneratorService.codeGenerator(codeGeneratorRequest);
        if (codeDetail.getId() > 0) {
            return new ResponseModel(Response.SUCCESS.getContent(), codeDetail).build(HttpStatus.CREATED);
        }
        return new ResponseModel(Errors.CODE_DETAIL_INVALID_REQUEST.getErrorDesc()).build(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/codegroups", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getAllCodeSet(
            @RequestParam(required = false, defaultValue = "1", value = "page") Integer page,
            @RequestParam(required = false, defaultValue = "5000", value = "per_page") Integer perPage,
            @RequestParam(required = false, defaultValue = "ASC",
                    value = "order") Sort.Direction direction,
            @RequestParam(required = false, defaultValue = "id", value = "sort") String sort,
            @RequestParam(required = false, value = "id") Long searchID,
            @RequestParam(required = false, value = "name") String searchName,
            @RequestParam(required = false, value = "type") String type) {
        return new ResponseModel(Response.SUCCESS.getContent(),
                codeGeneratorService.getAllCodeSet(page, perPage, direction, sort, searchID, searchName, type))
                .build(HttpStatus.OK);
    }

    @RequestMapping(value = "/codegroups/{codeSetId}", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getCode(@PathVariable Long codeSetId) {
        return new ResponseModel(Response.SUCCESS.getContent(),
                codeGeneratorService.getCodeDetail(codeSetId)).build(HttpStatus.OK);
    }

    @RequestMapping(value = "/codegroups/{codeSetId}/codes", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getCodesPage(@PathVariable Long codeSetId,
                                                  @RequestParam(required = false, defaultValue = "1",
                                                          value = "page") Integer page,
                                                  @RequestParam(required = false, defaultValue = "50",
                                                          value = "per_page") Integer perPage,
                                                  @RequestParam(required = false, defaultValue = "ASC",
                                                          value = "order") Sort.Direction direction,
                                                  @RequestParam(required = false, defaultValue = "id",
                                                          value = "sort") String sort) {
        return new ResponseModel(Response.SUCCESS.getContent(),
                codeGeneratorService.getCodePage(codeSetId, page, perPage, direction, sort)).build(HttpStatus.OK);
    }

    @RequestMapping(value = "/codegroups/{codeSetId}", method = RequestMethod.PUT,
            headers = {"Content-type=application/json"})
    public HttpEntity<ResponseModel> updateCode(@PathVariable Long codeSetId,
                                                @RequestBody CodeGeneratorRequest codeGeneratorRequest) {
        return new ResponseModel(Response.SUCCESS.getContent(),
                codeGeneratorService.updateCodeDetail(codeSetId, codeGeneratorRequest)).build(HttpStatus.OK);
    }

    @RequestMapping(value = "/codes", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getAllCodes(
            @RequestParam(required = false, defaultValue = "1", value = "page") Integer page,
            @RequestParam(required = false, defaultValue = "5000", value = "per_page") Integer perPage,
            @RequestParam(required = false, defaultValue = "ASC",
                    value = "order") Sort.Direction direction,
            @RequestParam(required = false, defaultValue = "id", value = "sort") String sort,
            @RequestParam(required = false, defaultValue = "", value = "q") String search) {
        return new ResponseModel(Response.SUCCESS.getContent(),
                codeGeneratorService.getAllCode(page, perPage, direction, sort, search)).build(HttpStatus.OK);
    }

    @RequestMapping(value = "/codes/{codeId}", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getCodeById(@PathVariable Long codeId) {
        return new ResponseModel(Response.SUCCESS.getContent(), codeGeneratorService.getCode(codeId))
                .build(HttpStatus.OK);
    }

    @RequestMapping(value = "/codegroups/{codeId}", method = RequestMethod.DELETE)
    public HttpEntity<ResponseModel> deleteCodeById(@PathVariable Long codeId) {
        return new ResponseModel(Response.SUCCESS.getContent(), codeGeneratorService.deleteCode(codeId))
                .build(HttpStatus.OK);
    }

    @RequestMapping(value = "/codes/search", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> searchCode(
            @RequestParam(required = false, defaultValue = "", value = "code") String code,
            @RequestParam(required = false, defaultValue = "", value = "codeset") String codeSet,
            @RequestParam(required = false, defaultValue = "1", value = "page") Integer page,
            @RequestParam(required = false, defaultValue = "5000", value = "per_page") Integer perPage,
            @RequestParam(required = false, defaultValue = "ASC",
                    value = "order") Sort.Direction direction,
            @RequestParam(required = false, defaultValue = "id", value = "sort") String sort) {
        if (!"".equals(code)) {
            List<Long> promotions = codeGeneratorService.findPromotionFromCode(code);
            return new ResponseModel(Response.SUCCESS.getContent(), promotions).build(HttpStatus.OK);
        } else if (!"".equals(codeSet)) {
            Page<CodeDetail> codeDetailPage = codeGeneratorService
                    .searchCodeDetailByName("%" + codeSet + "%", page, perPage, direction, sort);
            return new ResponseModel(Response.SUCCESS.getContent(), codeDetailPage).build(HttpStatus.OK);
        } else {
            return new ResponseModel(Errors.CODE_INVALID_REQUEST.getErrorDesc()).build(HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler(value = CodeNotFoundException.class)
    public HttpEntity<ResponseModel> handleCodeNotFoundException() {
        return new ResponseModel(Errors.CODE_NOT_FOUND.getErrorDesc()).build(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = CodeTypeException.class)
    public HttpEntity<ResponseModel> handleCodeTypeException() {
        return new ResponseModel(Errors.CODE_TYPE_NOT_VALID.getErrorDesc()).build(HttpStatus.BAD_REQUEST);
    }
}
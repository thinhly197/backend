package com.ascend.campaign.controllers;

import com.ascend.campaign.constants.Errors;
import com.ascend.campaign.constants.Response;
import com.ascend.campaign.entities.EmailGroup;
import com.ascend.campaign.exceptions.EmailGroupNotFoundException;
import com.ascend.campaign.models.ResponseModel;
import com.ascend.campaign.services.EmailGroupService;
import org.springframework.beans.factory.annotation.Autowired;
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

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/v1/emailgroups")
public class EmailGroupController {

    @NotNull
    EmailGroupService emailGroupService;

    @Autowired
    public EmailGroupController(EmailGroupService emailGroupService) {
        this.emailGroupService = emailGroupService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getAllEmailGroup(
            @RequestParam(required = false, defaultValue = "1", value = "page") Integer page,
            @RequestParam(required = false, defaultValue = "30", value = "per_page") Integer perPage,
            @RequestParam(required = false, defaultValue = "ASC", value = "order") Sort.Direction direction,
            @RequestParam(required = false, defaultValue = "id", value = "sort") String sort,
            @RequestParam(required = false, value = "id") Long searchID,
            @RequestParam(required = false, value = "name") String searchName) {
        return new ResponseModel(Response.SUCCESS.getContent(),
                emailGroupService.getAllEmailGroup(page, perPage, direction, sort, searchID, searchName))
                .build(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, headers = {"Content-type=application/json"})
    public HttpEntity<ResponseModel> createEmailGroup(@RequestBody EmailGroup emailGroup) {
        EmailGroup emailGroupResponse = emailGroupService.createEmailGroup(emailGroup);
        return new ResponseModel(Response.SUCCESS.getContent(), emailGroupResponse).build(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{emailGroupId}", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getEmailGroup(@PathVariable Long emailGroupId) {
        return new ResponseModel(Response.SUCCESS.getContent(),
                emailGroupService.getEmailGroup(emailGroupId)).build(HttpStatus.OK);
    }

    @RequestMapping(value = "/{emailGroupId}", method = RequestMethod.DELETE)
    public HttpEntity<ResponseModel> deleteEmailGroup(@PathVariable Long emailGroupId) {
        return new ResponseModel(Response.SUCCESS.getContent(),
                emailGroupService.deleteEmailGroup(emailGroupId)).build(HttpStatus.OK);
    }

    @RequestMapping(value = "/{emailGroupId}", method = RequestMethod.PUT)
    public HttpEntity<ResponseModel> updateEmailGroup(@PathVariable Long emailGroupId,
                                                      @RequestBody EmailGroup emailGroup) {
        return new ResponseModel(Response.SUCCESS.getContent(),
                emailGroupService.updateEmailGroup(emailGroupId, emailGroup)).build(HttpStatus.OK);
    }

    @ExceptionHandler(value = EmailGroupNotFoundException.class)
    public HttpEntity<ResponseModel> handleEmailGroupNotFoundException() {
        return new ResponseModel(Errors.EMAILGROUP_NOT_FOUND.getErrorDesc()).build(HttpStatus.NOT_FOUND);
    }
}
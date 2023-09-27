package br.com.muttley.security.infra.controller;

import br.com.muttley.security.feign.AccessPlanServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Joel Rodrigues Moreira on 23/09/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */

public class AccessPlanControler {
    private final AccessPlanServiceClient accessPlanService;

    @Autowired
    public AccessPlanControler(AccessPlanServiceClient accessPlanService) {
        this.accessPlanService = accessPlanService;
    }

    @RequestMapping(value = "/api/v1/access-plan/find-by-owner", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public final ResponseEntity findByOwner(@RequestParam("owner") final String owner) {
        return ResponseEntity.ok(this.accessPlanService.findByOwner(owner));
    }
}

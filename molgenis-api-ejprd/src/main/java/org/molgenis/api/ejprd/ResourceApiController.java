package org.molgenis.api.ejprd;

import static java.util.Objects.requireNonNull;

import javax.validation.Valid;
import org.molgenis.api.ApiNamespace;
import org.molgenis.api.ejprd.model.DataResponse;
import org.molgenis.api.ejprd.model.ResourceRequest;
import org.molgenis.api.ejprd.service.InternalResourceQueryService;
import org.molgenis.security.core.runas.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(ResourceApiController.BASE_URI)
public class ResourceApiController implements ResourceApi {

  private static final Logger LOG = LoggerFactory.getLogger(ResourceApiController.class);

  static final String BASE_URI = ApiNamespace.API_PATH + "/ejprd";

  private final InternalResourceQueryService resourceQueryService;

  private static final String apiVersion = "v1";

  ResourceApiController(InternalResourceQueryService resourceQueryService) {
    this.resourceQueryService = requireNonNull(resourceQueryService);
  }

  @GetMapping("/resource/search")
  @ResponseBody
  @RunAsSystem
  public DataResponse getResourceRequest(@Valid ResourceRequest resourceRequest) {
    String orphaCode = resourceRequest.getOrphaCode();
    String name = resourceRequest.getName();
    Integer skip = resourceRequest.getSkip();
    Integer limit = resourceRequest.getLimit();

    return resourceQueryService.query(orphaCode, name, skip, limit);
  }

  @GetMapping("/resource/{resourceId}")
  @ResponseBody
  @RunAsSystem
  public DataResponse getResourceById(@PathVariable("resourceId") String resourceId) {
    return resourceQueryService.getById(resourceId);
  }
}

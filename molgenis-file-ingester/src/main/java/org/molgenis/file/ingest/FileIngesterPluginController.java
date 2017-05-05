package org.molgenis.file.ingest;

import org.molgenis.data.jobs.schedule.JobScheduler;
import org.molgenis.ui.MolgenisPluginController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping(FileIngesterPluginController.URI)
public class FileIngesterPluginController extends MolgenisPluginController
{
	public static final String ID = "fileingest";
	public static final String URI = MolgenisPluginController.PLUGIN_URI_PREFIX + ID;
	private final JobScheduler scheduler;

	@Autowired
	public FileIngesterPluginController(JobScheduler scheduler)
	{
		super(URI);
		this.scheduler = scheduler;
	}

	@RequestMapping(method = GET)
	public String init()
	{
		return "view-file-ingest";
	}
}

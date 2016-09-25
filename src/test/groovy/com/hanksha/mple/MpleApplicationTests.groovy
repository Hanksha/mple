package com.hanksha.mple

import com.fasterxml.jackson.databind.ObjectMapper
import com.hanksha.mple.data.ProjectRepository
import com.hanksha.mple.data.model.message.editor.LevelOperation
import com.hanksha.mple.data.model.message.editor.TileOperation
import com.hanksha.mple.service.ProjectManager
import groovy.json.JsonOutput
import org.apache.commons.io.FileUtils
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.RequestPostProcessor
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

import java.nio.file.Files
import java.nio.file.Paths

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.*

@RunWith(SpringRunner)
@FixMethodOrder(MethodSorters.JVM)
@SpringBootTest
@WebAppConfiguration
@Ignore
class MpleApplicationTests {

	@Autowired
	WebApplicationContext webApplicationContext

	MockMvc mockMvc

	Authentication auth

	@Autowired
	ProjectManager projectManager

	@Autowired
	ProjectRepository projectRepo

	@Autowired
	ObjectMapper objectMapper

	@Before
	void setup() {
		mockMvc = new MockMvcBuilders().webAppContextSetup(webApplicationContext)
				.apply(springSecurity())
				.build()

		auth = new UsernamePasswordAuthenticationToken('admin', 'admin', [new SimpleGrantedAuthority("ROLE_ADMIN")])
	}

	@After
	void cleanup() {
		projectRepo.findAll().each {
			if(it.name == 'demo-project')
				return
			projectManager.deleteProject(it.name)
		}
	}

	/*@Test
	void createProjectRequest() {
		mockMvc.perform(post('/api/projects')
				.with(authentication(auth))
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content('test-project'))
				.andExpect(status().isOk())
	}

	@Test
	void deleteProjectRequest() {
		def projectName = 'to-be-deleted'

		mockMvc.perform(post('/api/projects')
				.with(authentication(auth))
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(projectName))
				.andExpect(status().isOk())

		mockMvc.perform(delete('/api/projects/' + projectName)
				.with(authentication(auth)))
				.andExpect(status().isOk())
	}

	@Test
	void testObjectMapper() {
		String json = '{"type":"tileOperation", "layerIndex": 0, "startRow": 0, "startCol": 0, "tiles": [[0]]}'
		assert objectMapper.readValue(json, LevelOperation).class == TileOperation
	}*/
}

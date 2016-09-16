package com.hanksha.mple.data

import com.hanksha.mple.data.model.Project

/**
 * Created by vivien on 9/12/16.
 */
interface ProjectRepository {

    Project findOne(String name)

    Project findOne(int id)

    List<Project> findAll()

    void save(Project project)

    void update(Project project)

    void delete(String name)

}

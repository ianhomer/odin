package com.purplepip.odin.server.rest;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.sequence.SequenceRuntime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by ian on 03/05/2017.
 */
@Controller
public class SequenceController {

  @Autowired
  SequenceRepository repository;

  @RequestMapping(value = "/sequences", method = RequestMethod.GET)
  HttpEntity<PagedResources<SequenceRuntime<Note>>> persons(Pageable pageable,
                                                            PagedResourcesAssembler assembler) {

    Page<SequenceRuntime<Note>> sequences = repository.findAll(pageable);
    return new ResponseEntity<>(assembler.toResource(sequences), HttpStatus.OK);
  }
}

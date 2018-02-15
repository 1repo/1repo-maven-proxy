package com.onerepo.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

@Controller
@Slf4j
public class MavenController {

  @Value("${1repo.mavenDir}")
  private String mavenDir;

  @Value("${1repo.remoteRepo}")
  private String remoteRepo;

  @GetMapping("/**")
  public void get(HttpServletResponse response, HttpServletRequest request) throws IOException {
    String filePath = getFilePath(request);
    log.info("ARTIFACT {}, REQUESTING", filePath);

    File dir = new File(mavenDir);
    File artifact = new File(dir, filePath);
    if (!artifact.exists()) {
      log.info("ARTIFACT {} NOT FOUND, DOWNLOADING");
      boolean downloaded = tryToDownloadFromRemote(filePath);
      if (!downloaded) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return;
      }
    } else {
      log.info("ARTIFACT {} FOUND, SERVING", filePath);
    }

    IOUtils.copy(new FileInputStream(artifact), response.getOutputStream());
  }

  private boolean tryToDownloadFromRemote(String filePath) {
    try {
      File dir = new File(mavenDir);
      File artifact = new File(dir, filePath);
      artifact.getParentFile().mkdirs();
      URL url = new URL(remoteRepo+filePath);
      FileUtils.copyURLToFile(url, artifact);
      log.info("ARTIFACT {} DOWNLOADED", filePath);
    } catch (IOException e) {
      log.info("ARTIFACT {} PROBLEMS", filePath);
      e.printStackTrace();
      return false;
    }
    return true;
  }

  private String getFilePath(HttpServletRequest request) {
    String path = (String) request.getAttribute( HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE );

    String bestMatchPattern = (String ) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

    AntPathMatcher apm = new AntPathMatcher();
    return apm.extractPathWithinPattern(bestMatchPattern, path);
  }

}

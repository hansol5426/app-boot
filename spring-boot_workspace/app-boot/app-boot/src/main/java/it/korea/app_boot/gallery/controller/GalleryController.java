package it.korea.app_boot.gallery.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/gal")
public class GalleryController {

    // 화면 만들기
    @GetMapping("")
    public ModelAndView getView(){
        ModelAndView view = new ModelAndView();
        view.setViewName("views/gal/galleryList");
        return view;
    }
}

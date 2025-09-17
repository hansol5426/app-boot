package it.korea.app_boot.board.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import it.korea.app_boot.board.service.BoardJPAService;

// 화면에 보여지는 컨트롤러
@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardJPAService service;

    @GetMapping("/list")
    public ModelAndView listView(){
        ModelAndView view = new ModelAndView();
        view.setViewName("views/board/boardList");
        return view;
    }
    
    // 게시글 수정화면 보기
    @GetMapping("/update/{brdId}")
    public ModelAndView updateForm(@PathVariable("brdId") int brdId){
        ModelAndView view = new ModelAndView();
        Map<String, Object> resultMap = new HashMap<>();
        
        try{
            resultMap = service.getBoard(brdId);
            view.addObject("vo", resultMap.get("vo"));
            
        } catch(Exception e){
            e.printStackTrace();
        }
        
        view.setViewName("views/board/updateForm");
        return view;
    }

    // 게시글 상세보기
    @GetMapping("/{brdId}")
    public ModelAndView detailView(@PathVariable("brdId") int brdId){
        ModelAndView view = new ModelAndView();
        Map<String, Object> resultMap = new HashMap<>();
        
        try{
            resultMap = service.getBoard(brdId);
            view.addObject("vo", resultMap.get("vo"));
            
        } catch(Exception e){
            e.printStackTrace();
        }
        
        view.setViewName("views/board/boardDetail");
        return view;
    }

    @GetMapping("/add/form")
    public ModelAndView writeForm(){
        ModelAndView view = new ModelAndView();
        view.setViewName("views/board/writeForm");
        return view;
    }
    
}

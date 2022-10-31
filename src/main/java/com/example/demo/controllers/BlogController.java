package com.example.demo.controllers;

import com.example.demo.models.*;
import com.example.demo.repo.*;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller

@PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
public class BlogController {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private CommRepository commRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ContactsRepository contactsRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String blogMain(Model model) {
        Iterable<Post> posts = postRepository.findAll();
        model.addAttribute("posts", posts);
        return "blog-main";
    }

    public String crntUsNm()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            return currentUserName;
        }

        return null;
    }

//    @GetMapping("/blog/add")
//    public String blogAdd(Model model) {
//        return "blog-add";
//    }
//
//    @PostMapping("/blog/add")
//    public String blogPostAdd(@RequestParam String title,
//                              @RequestParam String anons,
//                              @RequestParam String full_text, Model model) {
//        Post post = new Post(title, anons, full_text);
//        postRepository.save(post);
//
//        return "redirect:/";
//    }

    @GetMapping("/blog/add")
    public String blogAdd(Model model)
    {
        User user = userRepository.findByUsername(crntUsNm());
        Iterable<Theme> themes = themeRepository.findAll();

        model.addAttribute("user", user);
        model.addAttribute("themes", themes);

        return "blog-add";
    }

    @PostMapping("/blog/add")
    public String blogPostAdd(@ModelAttribute("post") @Valid Post post,
                              BindingResult bindingResult,
                              Model model) {
//        if (bindingResult.hasErrors())
//        {
//            User user = userRepository.findByUsername(crntUsNm());
//            Iterable<Theme> themes = themeRepository.findAll();
//
//            model.addAttribute("user", user);
//            model.addAttribute("themes", themes);
//
//            return "blog-add";
//        }

        post.setUser(userRepository.findByUsername(crntUsNm()));
        postRepository.save(post);

        return "redirect:/";
    }

    @GetMapping("/blog/filter")
    public String blogFilter(@RequestParam(defaultValue = "") String title,
                             @RequestParam(required = false) boolean accurate_search,
                             Model model)
    {
        if (!title.equals("")) {
            List<Post> results = accurate_search ? postRepository.findByTitle(title) : postRepository.findByTitleContains(title);
            model.addAttribute("results", results);
        }

        model.addAttribute("title", title);
        model.addAttribute("accurate_search", accurate_search);

        return "blog-filter";
    }

    @GetMapping("/blog/profile")
    public String blogProfileAdd(Model model)
    {
        User user = userRepository.findByUsername(crntUsNm());

        model.addAttribute("user", user);

        return "blog-profile";
    }

    @PostMapping("/blog/profile")
    public String blogPostProfile(@ModelAttribute ("profile") @Valid Profile profile,
                                  BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return "blog-profile";
        }

        return "redirect:/";

    }

    @GetMapping("/blog/comm")
    public String blogComments(Model model) {

        Iterable<Comm> comment = commRepository.findAll();
        model.addAttribute("comment", comment);

        return "blog-comm";
    }

    @GetMapping("/blog/comm/add")
    public String blogCommentsAdd(Comm comm, Model model)
    {
        Iterable<Comm> comms = commRepository.findAll();
        model.addAttribute("comms", comms);

        return "blog-comm-add";
    }

    @PostMapping("blog/comm/add")
    public String blogCommentsAdd(@ModelAttribute ("comm") @Valid Comm comm,
                                  BindingResult bindingResult)
    {
        if(bindingResult.hasErrors()){
            return "blog-comm-add";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.now();
        String formattedDateTime = dateTime.format(formatter); // "1986-04-08 12:30"

        comm.setActionDate(formattedDateTime);
        commRepository.save(comm);

        return "redirect:/blog/comm";
    }

//    @GetMapping("/blog/profile/{id}")
//    public String blogDetailsProfiles(@PathVariable(value = "id") long id, Model model)
//    {
//        if(!profileRepository.existsById(id))
//        {
//            return "redirect:/";
//        }
//
//        Optional<User> user = userRepository.findById(id);
//        ArrayList<user> res = new ArrayList<>();
//        profiles.ifPresent(res::add);
//        model.addAttribute("profiles", res);
//
//        return "blog-details-profiles";
//    }

    @GetMapping("/blog/profiles/{id}/edit")
    public String blogEditProfiles(@PathVariable(value = "id") long id, Model model)
    {
        if(!userRepository.existsById(id)){
            return "redirect:/";
        }

        Optional<User> user = userRepository.findById(id);
        model.addAttribute("user", user.get());

        return "blog-edit-profiles";
    }

    @PostMapping("blog/profiles/{id}/edit")
    public String blogProfilesUpdate(@ModelAttribute ("user") @Valid User user,
                                     BindingResult bindingResult)
    {
        if(bindingResult.hasErrors()){
            return "blog-edit-profiles";
        }

        user.setRoles(Collections.singleton(Role.USER));
        userRepository.save(user);
        return "redirect:/";
    }

    @PostMapping("/blog/profiles/{id}/remove")
    public String blogBlogProfilesDelete(@PathVariable("id") long id, Model model)
    {
        Profile profile = profileRepository.findById(id).orElseThrow();
        profileRepository.delete(profile);

        return "redirect:/";
    }

    @GetMapping("/blog/{id}")
    public String blogDetails(@PathVariable(value = "id") long id, Model model)
    {
        if(!postRepository.existsById(id))
        {
            return "redirect:/";
        }

        Optional<Post> post = postRepository.findById(id);
        ArrayList<Post> res = new ArrayList<>();
        post.ifPresent(res::add);
        model.addAttribute("post", res);

        return "blog-details";
    }

    @GetMapping("/blog/{id}/edit")
    public String blogEdit(@PathVariable(value = "id") long id, Post posts, Model model)
    {
        if(!postRepository.existsById(id)){
            return "redirect:/";
        }

        Optional<Post> post = postRepository.findById(id);
        model.addAttribute("post", post.get());

        return "blog-edit";
    }

    @PostMapping("blog/{id}/edit")
    public String blogPostUpdate(@ModelAttribute ("post") @Valid Post post,
                                 BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return "blog-edit";
        }

        postRepository.save(post);
        return "redirect:/";
    }

    @PostMapping("/blog/{id}/remove")
    public String blogBlogDelete(@PathVariable("id") long id, Model model) {
        Post post = postRepository.findById(id).orElseThrow();
        postRepository.delete(post);
        return "redirect:/";
    }

    @GetMapping("/blog/comm/{id}")
    public String blogDetailsComm(@PathVariable(value = "id") long id, Model model)
    {
        if(!commRepository.existsById(id))
        {
            return "redirect:/";
        }

        Optional<Comm> comm = commRepository.findById(id);
        ArrayList<Comm> res = new ArrayList<>();
        comm.ifPresent(res::add);
        model.addAttribute("comm", res);

        return "blog-details-comm";
    }

    @GetMapping("/blog/comm/{id}/edit")
    public String blogEditComm(@PathVariable(value = "id") long id, Model model)
    {
        Optional<Comm> comm = commRepository.findById(id);
//        ArrayList<Comm> res = new ArrayList<>();
//        comm.ifPresent(res::add);
        model.addAttribute("comm", comm.get ());

        return "blog-edit-comm";
    }

    @PostMapping("/blog/comm/{id}/edit")
    public String blogUpdateComm(@ModelAttribute ("comm") @Valid Comm comm,
                                 BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return "blog-edit-comm";
        }

        commRepository.save(comm);

        return "redirect:/blog/comm";
    }

    @PostMapping("/blog/comm/{id}/remove")
    public String blogBlogDeleteComm(@PathVariable("id") long id, Model model) {
        Comm comm = commRepository.findById(id).orElseThrow();
        commRepository.delete(comm);

        return "redirect:/blog/comm";
    }

    @GetMapping("/blog/filter/comm")
    public String blogFilterComments(@RequestParam(defaultValue = "") String author,
                                     @RequestParam(required = false) boolean accurate_search,
                                     Model model)
    {
        if (!author.equals("")) {
            List<Comm> results = accurate_search ? commRepository.findByAuthor(author) : commRepository.findByAuthorContains(author);
            model.addAttribute("results", results);
        }

        model.addAttribute("author", author);
        model.addAttribute("accurate_search", accurate_search);

        return "blog-comm-filter";
    }

    @GetMapping("/blog/filter/profile")
    public String blogFilterProfile(@RequestParam(defaultValue = "") String nickname,
                                    @RequestParam(required = false) boolean accurate_search,
                                    Model model)
    {
        if (!nickname.equals("")) {
            List<Profile> results = accurate_search ? profileRepository.findByNickname(nickname) : profileRepository.findByNicknameContains(nickname);
            model.addAttribute("results", results);
        }

        model.addAttribute("nickname", nickname);
        model.addAttribute("accurate_search", accurate_search);

        return "blog-profiles-filter";
    }
}


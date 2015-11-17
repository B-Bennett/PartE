package com.theironyard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by BennettIronYard on 11/13/15.
 */
@Controller
public class PartEController {
    @Autowired
    UserRepository users;

    @Autowired
    EntertainmentRepository entertainments;

    @PostConstruct
    public void init() throws InvalidKeySpecException, NoSuchAlgorithmException {
        User user = users.findOneByName("TheMan");
        if (user == null) {
            user = new User();
            user.name = "TheMan";
            user.password = PasswordHash.createHash("1234");
            users.save(user);
        }
    }

    @RequestMapping("/")
    public String home(HttpSession session, Model model,
                       String type, Integer price, String entertainmentName,
                       String showMine, @RequestParam(defaultValue = "0") int page) {

        PageRequest pr = new PageRequest(page, 5);
        Page p;

        String username = (String) session.getAttribute("username");//you have to cast (String)... //It should read the username from the session and add it to the model
        if (username ==null) {
            return "login";
        }
        if (entertainmentName != null) {
           // model.addAttribute("entertainment", users.findOneByName(username).entertainments);
            p = entertainments.findByName(pr, entertainmentName);
            model.addAttribute("entertainmentName", entertainmentName);
        }
        else if (type != null) {
           // model.addAttribute("entertainment", entertainments.searchByName(search));
            p = entertainments.findByType(pr, type);
            model.addAttribute("type", type);
        }
        else if (price != null) {
            //model.addAttribute("entertainment", entertainments.findByTypeAndPrice(type, price));
            p = entertainments.findByPrice(pr, price);
            model.addAttribute("price", price);
        }
        else {
            //model.addAttribute("entertainment", entertainments.findAll());
            p = entertainments.findAll(pr);
        }
        User user = users.findOneByName(username);
        model.addAttribute("isOwner", user.isOwner);

        model.addAttribute("nextPage", page+1);
        model.addAttribute("showNext", p.hasNext());

        return "home";
    }

    @RequestMapping("/addEntertainment")
    public String addEntertainment(String entertainmentName, String entertainmentType, int entertainmentPrice,HttpSession session) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in.");
        }
        User user = users.findOneByName(username);
        if (!user.isOwner) {
            throw new Exception("Not Owner");
        }
        Entertainment entertainment = new Entertainment();
        entertainment.name = entertainmentName;
        entertainment.type = entertainmentType;
        entertainment.price = entertainmentPrice;
        entertainment.rating = 0;
        entertainment.comment = "";
        entertainment.user = user;
        entertainments.save(entertainment);

        return "redirect:/";
    }
   @RequestMapping("/editRating")
    public String editRating( int entertainmentRating,int id,String comment ,HttpSession session) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in.");
        }
        Entertainment entertainment = entertainments.findOne(id);
        entertainment.rating = entertainmentRating;
       entertainment.comment = comment;
       entertainments.save(entertainment);

        return "redirect:/";
    }

    @RequestMapping("/editEntertainment")
    public String editEntertainment(int id, String entertainmentName, String entertainmentType, HttpSession session) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in.");
        }
        User user = users.findOneByName(username);
        if (!user.isOwner) {
            throw new Exception("Not Owner");
        }
        Entertainment entertainment = entertainments.findOne(id);
        entertainment.name = entertainmentName;
        entertainment.type = entertainmentType;
        entertainments.save(entertainment);

        return "redirect:/";
    }

    @RequestMapping("/deleteEntertainment")
    public String deleteEntertainment(int id) {
        entertainments.delete(id);

        return "redirect:/";
    }
    @RequestMapping("/edit")
    public String edit(int id, HttpSession session, Model model) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in.");
        }
        User user = users.findOneByName(username);
        model.addAttribute("isOwner", user.isOwner);
        model.addAttribute("entertainment", entertainments.findOne(id));

        return "edit";
    }


    @RequestMapping("/login")
    public String login(String username, String password,HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        session.setAttribute("username", username);

        User user = users.findOneByName(username);
        if (user == null) {
            user = new User();
            user.name = username;
            user.password = PasswordHash.createHash(password);
            user.isOwner = password.equals("admin");
            users.save(user);
        }
        else if (!PasswordHash.validatePassword(password, user.password)) {
            throw new Exception("Wrong Password");
        }
        return "redirect:/";
    }

    @RequestMapping("logout")
        public String logout(HttpSession session){
            session.invalidate();
        return "redirect:/";
    }

}

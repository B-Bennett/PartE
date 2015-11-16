package com.theironyard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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
                       String type, Integer price, String search,
                       String showMine) {

        String username = (String) session.getAttribute("username");//you have to cast (String)... //It should read the username from the session and add it to the model
        if (username ==null) {
            return "login";
        }
        if (showMine != null) {
            model.addAttribute("entertainment", users.findOneByName(username).entertainments);
        }
        else if (search != null) {
            model.addAttribute("entertainment", entertainments.searchByName(search));
        }
        else if (type != null && price != null) {
            model.addAttribute("entertainment", entertainments.findByTypeAndPrice(type, price));
        }
        else if (type != null) {
            model.addAttribute("entertainment", entertainments.findByTypeOrderByNameAsc(type));
        }
        else {
            model.addAttribute("entertainment", entertainments.findAll());
        }
        User user = users.findOneByName(username);
        model.addAttribute("isOwner", user.isOwner);
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
    public String deleteEntertainment(Integer id) {
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
    public String login(String username, String password, Boolean isOwner ,HttpServletRequest request) throws Exception {
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

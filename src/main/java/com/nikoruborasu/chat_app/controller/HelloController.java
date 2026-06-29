package com.nikoruborasu.chat_app.controller;

import java.nio.file.StandardCopyOption;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import jakarta.servlet.http.HttpSession;
import com.nikoruborasu.chat_app.entity.User;
import java.time.LocalDateTime;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;
import com.nikoruborasu.chat_app.entity.Message;
import com.nikoruborasu.chat_app.repository.MessageRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;

// このクラスはブラウザからのリクエストを受け取り、画面表示やデータ保存を行う
@Controller
public class HelloController {

    // データベースを操作するRepository
    private final MessageRepository messageRepository;

    // Repositoryを受け取るコンストラクタ(Springが自動で渡してくれる)
    public HelloController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    // "/"にアクセスしたときに実行される
    @GetMapping("/")
    public String hello(Model model,HttpSession session) {

        if(session.getAttribute("loginUser")==null){
            return "redirect:/login";
        }
        User loginUser = (User) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);
        // データベースからメッセージを新しい順に取得し、
        // "messages"という名前でHTMLへ渡す
        model.addAttribute("messages", messageRepository.findAllByOrderByIdDesc());

        // templates/index.html を表示
        return "index";
    }

    // 「送信」ボタンが押されたときに実行される
    @PostMapping("/send")
    public String send(
            // フォームのname欄を受け取る
            @RequestParam String name,

            // フォームのmessage欄を受け取る
            @RequestParam String message,
            @RequestParam MultipartFile image,
            Model model,
            HttpSession session) {

        if(name.isBlank()||message.isBlank()){
            model.addAttribute("messages", messageRepository.findAllByOrderByIdDesc());
            model.addAttribute("error", "名前とメッセージを入力してください");

            return "index";
        }

        User loginUser = (User) session.getAttribute("loginUser");

        model.addAttribute("loginUser", loginUser);

        // Messageオブジェクトを作成
        Message msg = new Message();

        // 名前をセット
        msg.setName(name);

        // メッセージをセット
        msg.setMessage(message);

        msg.setUsername(loginUser.getUsername());

        if (!image.isEmpty()) {

            String fileName = image.getOriginalFilename();

            Path path = Paths.get("src/main/resources/static/uploads/" + fileName);

            System.out.println(path.toAbsolutePath());
            System.out.println(Files.exists(path.getParent()));

            try {
                Files.copy(image.getInputStream(), path);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }

            msg.setImageName(fileName);
        }

        msg.setCreatedAt(LocalDateTime.now());

        // データベースへ保存
        messageRepository.save(msg);

        // 保存後はトップページへ戻る
        return "redirect:/";
    }
    //削除機能
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");

        Message msg = messageRepository.findById(id).orElse(null);
       //本人以外は削除できないようにしてる！
        if (msg != null &&
                msg.getUsername().equals(loginUser.getUsername())) {

            messageRepository.deleteById(id);
        }

        return "redirect:/";
    }
}
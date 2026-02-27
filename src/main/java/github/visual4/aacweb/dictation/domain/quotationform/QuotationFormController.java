package github.visual4.aacweb.dictation.domain.quotationform;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.Res;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.quotationform.dto.NewQuotationFormDto;
import github.visual4.aacweb.dictation.domain.user.User;
import github.visual4.aacweb.dictation.domain.user.UserService;

@RestController
@RequestMapping("/api/admin")
public class QuotationFormController {

  final QuotationFormService quotationFormService;
  final UserService userService;

  public QuotationFormController(QuotationFormService quotationFormService, UserService userService) {
    this.quotationFormService = quotationFormService;
    this.userService = userService;
  }

  @PostMapping("/quotation/new")
  public Object createQuotationForm(@RequestBody NewQuotationFormDto dto) {
    TypeMap res = quotationFormService.createQuotationForm(dto);
    return Res.success(res);
  }

  @GetMapping("/quotation")
  public Object listQuotationForms() {
    List<QuotationForm> forms = quotationFormService.findQuotationForms();
    return Res.success("quotationForms", forms);
  }

  @PutMapping("/quotation")
  public Object updateQuotationForm(@RequestBody QuotationForm form) {
    TypeMap res = quotationFormService.updateQuotationForm(form);
    return Res.success(res);
  }

  @PutMapping("/quotation/{seq}/close")
  public Object closeQuotationForm(@PathVariable Long seq) {
    QuotationForm form = quotationFormService.closeQuotationForm(seq);
    return Res.success("form", form);
  }

  @PostMapping("/quotation/user")
  public Object findQuotationUser(@RequestBody TypeMap form) {
    String email = form.getStr("email");
    User user = userService.findTeacherByEmail(email);
    return Res.success("user", user);
  }
}

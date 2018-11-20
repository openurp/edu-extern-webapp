[#ftl]
[@b.head/]
  [@b.toolbar title = "学生证书申请－<span style=\"color:blue\">第<span style=\"padding-left: 3px; padding-right: 3px\">3</span>步：获取证书</span>"]
    bar.addItem("取消返回", function() {
      bg.form.submit(document.searchForm);
    }, "backward.png");
  [/@]
  [#include "/component/certificate/selector2.ftl"/]
  [@b.form name="certificationForm" action="!apply" target="certifications" theme="list"]
    [@b.field label="学号"]<span>${std.user.code}</span>[/@]
    [@b.field label="姓名"]<span>${std.user.name}</span>[/@]
    [@b.field label="年级"]<span>${std.state.grade}</span>[/@]
    [@b.field label="院系"]<span>${std.state.department.name}</span>[/@]
    [@b.field label="专业"]<span>${std.state.major.name}</span>[/@]
    [@b.field label="方向"]<span>${(std.state.direction.name)!"<br>"}</span>[/@]
    [@b.field label="班级"]<span>${(std.state.adminclass.name)!"<br>"}</span>[/@]
    [@b.field label="身份证号"]<span>${std.person.code}</span>[/@]
    [@certSelects "certificationForm" "width: 200px"/]
    [@b.formfoot]
      <input type="hidden" name="no" value="${std.user.code}"/>
      <input type="hidden" name="stdId" value="${std.id}"/>
      <input type="hidden" name="settingId" value="${setting.id}"/>
      [@b.submit value="下一步"/]
    [/@]
  [/@]
[@b.foot/]

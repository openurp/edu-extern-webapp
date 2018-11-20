[#ftl]
[@b.head/]
  [@b.toolbar title = "学生证书申请－<span style=\"color:blue\">第<span style=\"padding-left: 3px; padding-right: 3px\">2</span>步：选择证书</span>"]
    bar.addItem("取消返回", function() {
      bg.form.submit(document.searchForm);
    }, "backward.png");
  [/@]
  [@b.form name="certificationForm" action="!apply" target="certifications" theme="list"]
    [@b.field label="学号"]<span>${std.user.code}</span>[/@]
    [@b.field label="姓名"]<span>${std.user.name}</span>[/@]
    [@b.field label="年级"]<span>${std.state.grade}</span>[/@]
    [@b.field label="院系"]<span>${std.state.department.name}</span>[/@]
    [@b.field label="专业"]<span>${std.state.major.name}</span>[/@]
    [@b.field label="方向"]<span>${(std.state.direction.name)!"<br>"}</span>[/@]
    [@b.field label="班级"]<span>${(std.state.adminclass.name)!"<br>"}</span>[/@]
    [@b.field label="身份证号"]<span>${std.person.code}</span>[/@]
    [#--2018-05-10 zhouqi 由于此地进入了“申请”（验证）环境，需要至少“数据来源”已经有效开放才能进入“申请”区域里面--]
    [#--                  按照完整性应该后面几步都要拦截，但由于这里是初次开发，一定要许多不完善的地方，所以这个验证环节，暂时仅在这个页面拦截--]
    [#assign comment][#if (examSubjects?size)?default(0) == 0]<span style="color: red">提示：当前没有你可以选择的证书，或还未开放。原因，“证书认定标准”和“数据来源”可能未配置。</span>[/#if][/#assign]
    [@b.select label="证书类型（大类）" name="examSubject.id" items=examSubjects?sort_by(["code"]) required="true" empty="请选择" style="width: 200px" comment=comment/]
    [@b.validity]
      var form = document.certificationForm;
      $("[name='examSubject.id']", form).require().assert(function() {
        var isOk = false;

        $.ajax({
          "type": "POST",
          "url": "${b.url("!apply2CheckAjax")}",
          "async": false,
          "dataType": "json",
          "data": {
            "examSubjectId": form["examSubject.id"].value
          },
          "success": function(data) {
            isOk = data.isOk;
          }
        });

        return isOk;
      }, "当前所选择的证书已经停止开放！！！");
    [/@]
    [@b.formfoot]
      <input type="hidden" name="no" value="${std.user.code}"/>
      <input type="hidden" name="stdId" value="${std.id}"/>
      [@b.submit value="下一步"/]
    [/@]
  [/@]
  <script>
    $(function() {
      $(document).ready(function() {
        $(document.certificationForm).find(".title").css("width", "115px");
      });
    });
  </script>
[@b.foot/]

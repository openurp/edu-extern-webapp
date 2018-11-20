[#ftl]
[#if isOk]
  [@b.form name="certificationForm" action="!applySave" target="certifications" theme="list"]
    [@b.field label="学号"]<span>${std.user.code}</span>[/@]
    [@b.field label="姓名"]<span>${std.user.name}</span>[/@]
    [@b.field label="年级"]<span>${std.state.grade}</span>[/@]
    [@b.field label="院系"]<span>${std.state.department.name}</span>[/@]
    [@b.field label="专业"]<span>${std.state.major.name}</span>[/@]
    [@b.field label="方向"]<span>${(std.state.direction.name)!"<br>"}</span>[/@]
    [@b.field label="班级"]<span>${(std.state.adminclass.name)!"<br>"}</span>[/@]
    [@b.field label="身份证号"]<span>${std.person.code}</span>[/@]
    [@b.field label="证书类型"]<span>${certificate.type.examSubject.name}</span>[/@]
    [#list certItems as item]
      [@b.field label=item[0].name]<span>${item[1].name}</span>[/@]
    [/#list]
    [@b.field label="获取状态"]<span style="color: green">已取得！</span>[/@]
    [@b.field label="证书编号"]<span style="color: green">${certCode}</span>[/@]
    [@b.field label="证书成绩"]<span style="color: green">${score}</span>[/@]
    [@b.formfoot]
      <input type="hidden" name="stdId" value="${std.id}"/>
      <input type="hidden" name="certCode" value="${certCode}"/>
      <input type="hidden" name="score" value="${score}"/>
      <input type="hidden" name="certificateId" value="${certificate.id}"/>
      [@b.submit id="apply4ok" value="提交"/]
    [/@]
  [/@]
  <script>
    $(function() {
      $(document).ready(function() {
        var form = document.certificationForm;

        var certification = {};
        certification.stdId = form.stdId.value;
        certification.certCode = form.certCode.value;
        certification.score = form.score.value;
        certification.certificateId = form.certificateId.value;

        console.log(certification);

        var apply4okObj = $("#apply4ok");
        apply4okObj.attr("onclick", "return false;");

        apply4okObj.click(function() {
          for (var field in certification) {
            form[field].value = certification[field];
          }

          console.log(form);
          bg.form.submit(form);
        });
      });
    });
  </script>
[#else]
  [@b.form name="certificationForm" action="!apply" target="certifications" theme="list"]
    [@b.field label="学号"]<span>${std.user.code}</span>[/@]
    [@b.field label="姓名"]<span>${std.user.name}</span>[/@]
    [@b.field label="年级"]<span>${std.state.grade}</span>[/@]
    [@b.field label="院系"]<span>${std.state.department.name}</span>[/@]
    [@b.field label="专业"]<span>${std.state.major.name}</span>[/@]
    [@b.field label="方向"]<span>${(std.state.direction.name)!"<br>"}</span>[/@]
    [@b.field label="班级"]<span>${(std.state.adminclass.name)!"<br>"}</span>[/@]
    [@b.field label="身份证号"]<span>${std.person.code}</span>[/@]
    [@b.field label="证书类型"]<span>${certificate.type.examSubject.name}</span>[/@]
    [#list certItems as item]
      [@b.field label=item[0].name]<span>${item[1].name}</span>[/@]
    [/#list]
    [@b.field label="获取状态"]<span style="color: red">该证书不存在！！！</span>[/@]
  [/@]
[/#if]

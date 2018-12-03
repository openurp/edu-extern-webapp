[#ftl]
[@b.head/]
  [@b.toolbar title="校外科目大类配置"]
    bar.addBack();
  [/@]
  [@b.form name="externGradeForm" action="!save" target="externGrades" theme="list"]
    [#assign elementSTYLE = "width: 200px"/]
    [#if (externGrade.id)?exists]
      [@b.field label="学号"]<span style="display: inline-block;">${(externGrade.std.user.code)!}</span><input type="hidden" name="externGrade.std.id" value="${(externGrade.std.id)!}"/>[/@]
      [@b.field label="姓名"]<span style="display: inline-block;">${(externGrade.std.user.name)!}</span>[/@]
    [#else]
      [@b.textfield label="学号" name="stdCode" value=(externGrade.std.user.code)! required="true" maxlength="30" style=elementSTYLE comment="在左边输入学号后，点击页面空白处，即可获取该学生信息"/]
      [@b.field label="姓名"]<span id="stdName" style="display: inline-block;"><br></span><input id="stdId" type="hidden" name="externGrade.std.id" value="${(externGrade.std.id)!}"/>[/@]
    [/#if]
    [@b.validity]
      $("#stdId", document.externGradeForm).require("请在上面输入一个有效的学号获取学生，谢谢！").assert(function() {
        return document.externGradeForm["externGrade.courseName"].value.trim().length > 0 && document.externGradeForm["externGrade.acquiredOn"].value.trim().length > 0;
      }, "外校课程、获得日期没有填写！").assert(function() {
        var isOk = false;

        $.ajax({
          "type": "POST",
          "url": "${b.url("!checkAjax")}",
          "async": false,
          "dataType": "json",
          "data": {
            "id": document.externGradeForm["externGrade.id"].value,
            "stdId": document.externGradeForm["externGrade.std.id"].value,
            "courseName": document.externGradeForm["externGrade.courseName"].value,
            "acquiredOn": document.externGradeForm["externGrade.acquiredOn"].value
          },
          "success": function(data) {
            isOk = data.isOk;
          }
        });

        return isOk;
      }, "该学生已存在于当前所填“获得日期”时所获得的“外校课程”记录了！！！");
    [/@]
    [@b.select label="校外学校" name="externGrade.school.id" items=schools empty="..." required="true" value=(externGrade.school.id)! style=elementSTYLE/]
    [@b.select label="培养层次" name="externGrade.level.id" items=levels empty="..." required="true" value=(externGrade.level.id)! style=elementSTYLE/]
    [@b.select label="教育类别" name="externGrade.category.id" items=eduCategories empty="..." required="true" value=(externGrade.category.id)! style=elementSTYLE/]
    [@b.textfield label="外校专业" name="externGrade.majorName" value=(externGrade.majorName)! required="true" maxlength="100" style=elementSTYLE/]
    [@b.textfield label="外校课程" name="externGrade.courseName" value=(externGrade.courseName)! required="true" maxlength="100" style=elementSTYLE/]
    [@b.textfield label="外校学分" name="externGrade.credits" value=(externGrade.credits)! required="true" maxlength="5" check="match('number')" style=elementSTYLE/]
    [@b.textfield label="外校得分" name="externGrade.scoreText" value=(externGrade.scoreText)! required="true" maxlength="5" check="match('number')" style=elementSTYLE/]
    [@b.datepicker label="获得日期" name="externGrade.acquiredOn" value=(externGrade.acquiredOn?string('yyyy-MM-dd'))! format="yyyy-MM-dd" required="true" style=elementSTYLE/]
    <div style="margin-left: 50px;color: blue">说明：一个学生相同获得日期相同课程只能出现一次。</div>
    [@b.formfoot]
      <input type="hidden" name="externGrade.id" value="${(externGrade.id)!}"/>
      <input type="hidden" name="_params" value="${b.paramstring}" />
      [@b.submit value="提交"/]
    [/@]
  [/@]
  [#if !(externGrade.id)?exists]
  <script>
    $(function() {
      function init(form) {
        var formObj = $(form);
        var stdNameObj = formObj.find("#stdName");

        formObj.find("[name=stdCode]").blur(function() {
          var thisObj = $(this);
          thisObj.parent().find(".error").remove();
          thisObj.parent().next().find(".error").remove();
          stdNameObj.empty();
          form["externGrade.std.id"].value = "";

          var code = thisObj.val().trim();
          if (code.length == 0) {
            throwError(thisObj.parent(), "请输入一个有效的学号，谢谢！");
            stdNameObj.html("<br>");
          } else {
            $.ajax({
              "type": "POST",
              "url": "${b.url("!loadStdAjax")}",
              "async": false,
              "dataType": "json",
              "data": {
                "code": code
              },
              "success": function(data) {
                if (data.id) {
                  stdNameObj.text(data.user.name);
                  form["externGrade.std.id"].value = data.id;
                } else {
                  throwError(thisObj.parent().next(), "请输入一个有效的学号，谢谢！");
                  stdNameObj.html("<br>");
                  thisObj.val("");
                }
              }
            });
          }
        });

        formObj.find(":submit").click(function() {
          var errObj = formObj.find("[name=stdCode]").parent().find(".error");
          if (errObj.size()) {
            formObj.find("[name=stdCode]").parent().append(errObj);
          }
        });
      }

      function throwError(parentObj, msg) {
        var errObj = parentObj.find(".error");
        if (!errObj.size()) {
          errObj = $("<label>");
          errObj.addClass("error");
          parentObj.append(errObj);
        }
        errObj.text(msg);
      }

      $(document).ready(function() {
        init(document.externGradeForm);
      });
    });
  </script>
  [/#if]
[@b.foot/]

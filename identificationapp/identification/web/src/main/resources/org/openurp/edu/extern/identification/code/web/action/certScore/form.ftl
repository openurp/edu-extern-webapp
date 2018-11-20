[#ftl]
[@b.head/]
  <script language="JavaScript" type="text/JavaScript" src="${base}/static/scripts/chosen/ajax-chosen.js"></script>
  [@b.toolbar title="<span style=\"color: blue\">" + (certScore.id)?exists?string("修改", "添加") + "</span>证书认定成绩标准配置"]
    bar.addItem("返回", function() {
        bg.form.submit(document.searchForm, "${b.url("!search")}", "certScores");
      }, "backward.png");
  [/@]
  [#include "/component/certificate/selector.ftl"/]
  [@b.form name="certScoreForm" action="!save" target="certScores" theme="list"]
    [#assign styleHTML = "width: 200px"/]
    [#assign examSubjectElement = { "name": "certScore.examSubject.id", "value": (certScore.examSubject.id)!, "required": "true" }/]
    [#assign certTypeElement = { "name": "certScore.certType.id", "value": (certScore.certType.id)!, "empty": "全部", "comment": "（数据来自“证书配置”）" }/]
    [#assign certLevelElement = { "name": "certScore.certLevel.id", "value": (certScore.certLevel.id)!, "empty": "全部", "comment": "（数据来自“证书配置”）" }/]
    [#assign divisionElement = { "name": "certScore.division.id", "value": (certScore.division.id)!, "empty": "全国", "comment": "（数据来自“证书配置”）" }/]
    [#assign examTimeElement = { "name": "certScore.examTime.id", "value": (certScore.examTime.id)!, "empty": "全部", "comment": "（数据来自“证书配置”）" }/]
    [@certSelects "certScoreForm" examSubjectElement certTypeElement certLevelElement divisionElement examTimeElement styleHTML/]
    [@b.validity]
      function check() {
        var isOk = false;

        $.ajax({
          "type": "POST",
          "url": "${b.url("!checkAjax")}",
          "async": false,
          "dataType": "json",
          "data": {
            "id": document.certScoreForm["certScore.id"].value,
            "examSubjectId": document.certScoreForm["certScore.examSubject.id"].value,
            "certTypeId": document.certScoreForm["certScore.certType.id"].value,
            "certLevelId": document.certScoreForm["certScore.certLevel.id"].value,
            "divisionId": document.certScoreForm["certScore.division.id"].value,
            "examTimeId": document.certScoreForm["certScore.examTime.id"].value
          },
          "success": function(data) {
            isOk = data.isOk;
          }
        });

        return isOk;
      }

      $("[name='certScore.certType.id']", document.certScoreForm).assert(function() {
        return check();
      }, "当前的配置数据已存在！！！");

      $("[name='certScore.examSubject.id']", document.certScoreForm).assert(function() {
        return check();
      }, "当前的配置数据已存在！！！");

      $("[name='certScore.certLevel.id']", document.certScoreForm).assert(function() {
        return check();
      }, "当前的配置数据已存在！！！");

      $("[name='certScore.division.id']", document.certScoreForm).assert(function() {
        return check();
      }, "当前的配置数据已存在！！！");

      $("[name='certScore.examTime.id']", document.certScoreForm).assert(function() {
        return check();
      }, "当前的配置数据已存在！！！");
    [/@]
    [@b.field label="可替课程" required="true"]
[#assign courseIds = ""/]
[#assign tbody]
                [#list (certScore.courses)! as csCourse]
                <tr class="${(csCourse_index % 2 == 0)?string("griddata-even", "griddata-odd")}">
                  <td>${csCourse.course.code}<input type="hidden" name="csCourse${csCourse_index}.course.id" value="${csCourse.course.id}"/></td>
                  <td>${csCourse.course.name}</td>
                  <td>${csCourse.course.credits}</td>
                  <td><input id="csCourse${csCourse_index}_score" type="text" name="csCourse${csCourse_index}.score" value="${(csCourse.score?string("#.####"))!}" maxlength="5" style="width: 50px; text-align: center"/>（<input id="csCourse${csCourse_index}_scoreValue" type="text" name="csCourse${csCourse_index}.scoreValue" value="${(csCourse.scoreValue)!}" maxlength="10" style="width: 50px; text-align: center"/>）</td>
                  <td><button id="btnRemoveCourse" type="button">删除</button><input type="hidden" name="csCourse${csCourse_index}.id" value="${(csCourse.id)!}"/></td>
                </tr>
                  [#assign courseIds = courseIds + (courseIds?length != 0)?string(",", "") + csCourse.course.id/]
                [/#list]
[/#assign]
      <table width="80%" cellspacing="0" cellpadding="0">
        <tr>
          <td><button id="btnAddCourse" type="button">添加</button><input id="courseIds_" type="hidden" name="courseIds" title="可替课程" value="${(courseIds)!}"/></td>
        </tr>
        <tr>
          <td style="padding-top: 5px; padding-bottom: 5px"><span style="color: blue">说明：如果勿“删除”了已保存的“可替课程”数据，在还未提交前，再添加该“可替课程”数据恢复，或点击右上角的“返回”取消本次配置。</span></td>
        </tr>
        <tr>
          <td>
            <table class="gridtable">
              <thead class="gridhead">
                <th width="100px">课程代码</th>
                <th width="200px">课程名称</th>
                <th width="50px">学分</th>
                <th>本校得分（显示）</th>
                <th width="100px">操作</th>
              </thead>
              <tbody>
${tbody}
              </tbody>
            </table>
          </td>
        </tr>
      </table>
    [/@]
    [@b.validity]
      $("[name=courseIds]", document.certScoreForm).require("请至少添加一门可替课程").match("notBlank");

      if (csCoursesValidity) {
        csCoursesValidity();
      }
    [/@]
    [@b.datepicker id="beginOn" label="启用日期" name="certScore.beginOn" value=(certScore.beginOn?string('yyyy-MM-dd'))?default('') required="true" style="width:200px" format="yyyy-MM-dd" maxDate="#F{$dp.$D(\\'endOn\\')}"/]
    [@b.datepicker id="endOn" label="截止日期" name="certScore.endOn" value=(certScore.endOn?string('yyyy-MM-dd'))?default('') style="width:200px" format="yyyy-MM-dd" minDate="#F{$dp.$D(\\'beginOn\\')}"/]
    [@b.formfoot]
      <input type="hidden" name="certScore.id" value="${(certScore.id)!}"/>
      [@b.submit value="提交"/]
    [/@]
  [/@]
  <script>
    $(function() {
      $(document).ready(function() {
        var form = document.certScoreForm;

        var btnAddCourse = $("button#btnAddCourse");

        btnAddCourse.click(function() {
          var paramDataMap = {};
          paramDataMap.id = form["certScore.id"].value;
          paramDataMap.courseIds = form["courseIds"].value;

          $(this).colorbox({
            "transition": "none",
            "title": "添加选择可选择的可替课程",
            "overlayClose": false,
            "speed": 0,
            "width": "800px",
            "height": "600px",
            "href": "${base}/identification/code/cert-score!coursesAjax.action",
            "data": paramDataMap
          });
        });

        window.btnAddCourse = btnAddCourse;
        window.loadBtnRemoveEvent = loadBtnRemoveEvent;
        form.onsubmit = function() {
          var rs = onsubmitcertScoreForm();
          if (rs) {
            var courseIds = form["courseIds"].value.replace(/\s/mg, "").split(",");
            for (var i = 0; i < courseIds.length; i++) {
              if (!form["csCourse" + i + ".scoreValue"].value.replace(/\s/mg, "")) {
                form["csCourse" + i + ".scoreValue"].value = form["csCourse" + i + ".score"].value;
              }
            }
          } else {
            $(".error[for$=scoreValue]").each(function() {
              $(this).appendTo($(this).parent());
            });
          }
          return rs;
        };

        loadBtnRemoveEvent();
      });

      [#--在ajax的过程中会调用--]
      function loadBtnRemoveEvent() {
        $("button#btnRemoveCourse").click(function() {
          if (confirm("要删除第 " + ($(this).parent().parent().index() + 1) + " 行“可替课程”配置数据吗？")) {
            $(this).parent().parent().remove();

            [#--重序“可替课程”的表单name--]
            var tbodyObj = btnAddCourse.parent().parent().parent().find("table").find("tbody");
            var i = 0;

            var courseIds = "";
            tbodyObj.children().each(function(r, trObj) {
              $(trObj).attr("class", (i % 2 == 0 ? "griddata-even" : "griddata-odd"));

              $(trObj).find("input").each(function(d, inputObj) {
                var name = $(inputObj).attr("name");
                var nameSections = name.split(".");
                nameSections[0] = "csCourse" + i;
                name = nameSections.join(".");
                $(inputObj).attr("name", name);
                if ("course" == nameSections[1]) {
                  courseIds += (courseIds.length > 0 ? "," : "") + document.certScoreForm[name].value;
                }
              });
              i++;
            });
            document.certScoreForm["courseIds"].value = courseIds;
          }
        });
      }
    });
  </script>
[@b.foot/]

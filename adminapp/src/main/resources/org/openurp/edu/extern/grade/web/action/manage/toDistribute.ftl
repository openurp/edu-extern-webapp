[#ftl]
[@b.head/]
  [@b.toolbar title = "<span style=\"color:blue\">" + externExamGrade.std.user.name + "（<span style=\"padding-left: 1px; padding-right: 1px\">" + externExamGrade.std.user.code + "</span>）" + externExamGrade.subject.name + "</span>证书课程及成绩认定管理"]
    bar.addItem("认定", function() {
      var fillSize = 0;
      var errorSize = 0;
      var planCourseIds = "";

      var form = document.externExamGradeDistributeForm;
      $(form).find("[name^=scoreText]").each(function() {
        if ($(this).val().trim().length && $(this).prev().val().trim().length) {
          fillSize++;
          planCourseIds += (planCourseIds.length > 0 ? "," : "") + $(this).prev().prev().val();
        } else if ($(this).val().trim().length || $(this).prev().val().trim().length) {
          errorSize++;
        }
      });

      if (fillSize && !errorSize) {
        bg.form.addInput(form, "planCourseIds", planCourseIds);
        bg.form.submit(form, "${b.url("!distribute")}");
      } else {
        alert("请完整填写要认定的课程成绩，谢谢。");
      }
    }, "${b.theme.iconurl("actions/new.png")}");

    bar.addItem("返回", function() {
      bg.form.submit(document.externExamGradeDistributeForm, "${b.url("!distributedList")}");
    }, "backward.png");
  [/@]
  [@b.form name="externExamGradeDistributeForm" action="!distribute" target="examGradeList"]
  <table class="gridtable">
    <thead class="gridhead">
      <tr>
        <th>课程代码</th>
        <th>课程名称</th>
        <th>课程类别</th>
        <th width="50px">学分</th>
        <th width="200px">成绩记录方式</th>
        <th width="200px">成绩（显示）</th>
        <th>修读类别</th>
        <th>考核方式</th>
        <th>是否免听</th>
      </tr>
    </thead>
    <tbody><input type="hidden" name="examGrade.id" value="${externExamGrade.id}"/>
      [#list planCourses?sort_by(["terms"]) as planCourse]
      <tr class="${(0 == planCourse_index % 2)?string("griddata-even", "griddata-odd")}">
        <td>${planCourse.course.code}</td>
        <td>${planCourse.course.name}</td>
        <td>${planCourse.course.courseType.name}</td>
        <td>${planCourse.course.credits}</td>
        <td>
          <select name="gradingMode.id${planCourse.id}" style="width: 150px">
            [#list gradingModes as gradingMode]
            <option value="${gradingMode.id}"[#if 1 == gradingMode.id] selected[/#if]>${gradingMode.name}</option>
            [/#list]
          </select>
        </td>
        <td><input type="hidden" name="planCourse.id${planCourse.id}" value="${planCourse.id}" maxlength="5" style="width: 50px"/><input type="text" name="score${planCourse.id}" value="" maxlength="5" style="width: 50px"/>（<input type="text" name="scoreText${planCourse.id}" value="" maxlength="10" style="width: 50px"/>）</td>
        <td>正常</td>
        <td>${planCourse.course.examMode.name}</td>
        <td>是</td>
      </tr>
      [/#list]
    </tbody>
  </table>
  [/@]
[@b.foot/]

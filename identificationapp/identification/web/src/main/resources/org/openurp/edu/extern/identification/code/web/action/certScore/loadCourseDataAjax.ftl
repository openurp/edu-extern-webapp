[#ftl]
[#assign courseDataRow = ""/]
[#assign courseIds = ""/]
[#assign csCoursesValidity = ""/]
[#assign k = (Parameters["ids"]?split(",")?length)?default(0)/]
[#list csCourses as csCourse]
  [#assign dataRow]
<tr class="${(csCourse_index % 2 == 0)?string("griddata-even", "griddata-odd")}">
  <td>${csCourse.course.code}<input type="hidden" name="csCourse${csCourse_index + k}.course.id" value="${csCourse.course.id}"/></td>
  <td>${csCourse.course.name}</td>
  <td>${csCourse.course.credits}</td>
  <td><input id="csCourse${csCourse_index + k}_score" type="text" name="csCourse${csCourse_index + k}.score" value="${(csCourse.score?string("#.####"))!}" maxlength="5" style="width: 50px; text-align: center"/>（<input id="csCourse${csCourse_index + k}_scoreValue" type="text" name="csCourse${csCourse_index + k}.scoreValue" value="${(csCourse.scoreValue)!}" maxlength="10" style="width: 50px; text-align: center"/>）</td>
  <td><button id="btnRemoveCourse" type="button">删除</button><input type="hidden" name="csCourse${csCourse_index + k}.id" value="${(csCourse.id)!}"/></td>
</tr>
  [/#assign]
  [#assign courseDataRow = courseDataRow + dataRow/]
  [#assign courseIds = courseIds + (courseIds?length != 0)?string(",", "") + csCourse.course.id/]
  [#assign csCourseValidity]$("[name=&apos;csCourse${csCourse_index + k}.score&apos;]", document.certScoreForm).require("请填写").match("number", "应为正数").greaterThan(0, "应为正数");$("[name=&apos;csCourse${csCourse_index + k}.scoreValue&apos;]", document.certScoreForm).require("请填写");[/#assign]
  [#assign csCoursesValidity = csCoursesValidity + csCourseValidity/]
[/#list]
{
  "courseDataRow": "${courseDataRow?js_string?replace("&apos;", "'")}",
  "courseIds": "${courseIds}",
  "csCoursesValidity": "${csCoursesValidity?js_string?replace("&apos;", "'")}"
}

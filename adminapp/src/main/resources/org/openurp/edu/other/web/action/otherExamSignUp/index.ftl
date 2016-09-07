[#ftl/]
[@b.head/]
[@b.toolbar title="报名管理 "/]
<div>
    <table class="indexpanel">
        <tr>
            <td class="index_view">
            [@b.form theme="search" name="otherExamSignUpsearchForm" action="!search" title="ui.searchForm" target="otherExamSignUpList"]
                <input type="hidden" name="info" value="${(info)!}"/>
                [@b.select name="fake.signupSeason.id" label="报名开关"]
                    <option value="">...</option>
                    [#list seasons! as season]
                        <option value="${season.id}">${season.name}</option>
                    [/#list]
                [/@]
                 [@eams.semesterCalendar theme="search" label="学年学期" name="semester.id" empty=true value=semester/]
                <input type="hidden" name="otherExamSignUp.std.project.id" value="${Session['projectId']}" />
                <input type="hidden" name="project.id" value="${Session['projectId']}" />
                [@b.textfield name="otherExamSignUp.std.code" label="std.code"/]
                [@b.textfield name="otherExamSignUp.std.name" label="std.name"/]
                [@b.textfield name="otherExamSignUp.std.grade" label="std.grade"/]
                [@b.select name="otherExamSignUp.std.education.id" id="educationTypeId" label="entity.education" items=educations empty="..."/]
                [@b.select name="otherExamSignUp.std.department.id" label="entity.department" items=departments empty="..." /]
                [@b.select name="otherExamSignUp.subject.category.id" label="考试类型" items=otherExamCategories empty="..."/]
                [@b.select name="otherExamSignUp.subject.id" label="报名科目" items=otherExamSubjects empty="..." /]
                [@b.select name="otherExamSignUp.campus.id" label="entity.campus" items=campuses empty="..." /]
                [#--][@b.textfield name="otherExamSignUp.examNo" label="准考证号"/][--]
                [@b.textfield name="otherExamSignUp.std.adminclass.name" label="班级名称"/]
                [@b.startend label="entity.signUpAt,~" name="signUpAt_start,signUpAt_end"  /]
                  [#--][@b.select name="fieldVisable" label="扩展字段" items={'1':'${b.text("common.yes")}','0':'${b.text("common.no")}'} empty="..." /][--]
                [@b.select name="otherExamSignUp.takeBus" label="乘坐校车" items={'1':'${b.text("common.yes")}','0':'${b.text("common.no")}'} empty="..." /]
            [/@]
            </td>
            <td class="index_content">
                [@b.div id="otherExamSignUpList" href="!search?semester.id="+(semester.id)!/]
            </td>
        </tr>
    </table>
</div>
[@b.foot/]
[#ftl/]
[#list subjects as subject]
    <option value="${subject.id}">${(subject.name)!}</option>
[/#list]
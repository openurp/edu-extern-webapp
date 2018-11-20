/*
 * OpenURP, Agile University Resource Planning Solution.
 *
 * Copyright © 2014, The OpenURP Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful.
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openurp.edu;

import org.beangle.commons.entity.orm.AbstractPersistModule;

/**
 * @author zhouqi 2018年11月13日
 */
public class IdentificationModule extends AbstractPersistModule {

  @Override
  protected void doConfig() {
    add(org.openurp.edu.extern.identification.model.Certificate.class,
        org.openurp.edu.extern.identification.model.Certification.class,
        org.openurp.edu.extern.identification.model.CertificationCourse.class,
        org.openurp.edu.extern.identification.model.CertScore.class,
        org.openurp.edu.extern.identification.model.CertScoreCourse.class,
        org.openurp.edu.extern.identification.model.ExternExamSubjectField.class,
        org.openurp.edu.extern.identification.model.ExternExamSubjectSetting.class);
  }
}

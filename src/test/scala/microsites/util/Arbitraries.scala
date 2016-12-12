/*
 * Copyright 2016 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package microsites.util

import java.io.File

import microsites._
import org.scalacheck.Arbitrary
import org.scalacheck.Gen._

trait Arbitraries {

  implicit def fileArbitrary: Arbitrary[File] = Arbitrary {
    uuid map (randomFileName => new File(randomFileName.toString))
  }

  implicit def paletteMapArbitrary: Arbitrary[Map[String, String]] = Arbitrary {
    for {
      stringList <- listOfN[String](6, Arbitrary.arbitrary[String])
      map        <- (stringList map (s => s -> s"value of $s")).toMap
    } yield map
  }

  implicit def defaultItemArbitrary: Arbitrary[DefaultItem] = Arbitrary {
    for {
      scope  ← paletteMapArbitrary.arbitrary
      values ← paletteMapArbitrary.arbitrary
    } yield DefaultItem(scope, values)
  }

  implicit def listDefaultsArbitrary: Arbitrary[List[DefaultItem]] = Arbitrary {
    for {
      n    ← oneOf(0, 100)
      list ← listOfN[DefaultItem](n, defaultItemArbitrary.arbitrary)
    } yield list
  }

  implicit def collectionItemArbitrary: Arbitrary[CollectionItem] = Arbitrary {
    for {
      output ← Arbitrary.arbitrary[Boolean]
      values ← paletteMapArbitrary.arbitrary
    } yield CollectionItem(output, values)
  }

  implicit def collectionMapArbitrary: Arbitrary[Map[String, CollectionItem]] = Arbitrary {
    for {
      stringList <- listOfN[String](6, Arbitrary.arbitrary[String])
      colItem    <- collectionItemArbitrary.arbitrary
      map        <- (stringList map (s => s -> colItem)).toMap
    } yield map
  }

  implicit def configYamlArbitrary: Arbitrary[ConfigYaml] = Arbitrary {
    for {
      name        ← Arbitrary.arbitrary[String]
      description ← Arbitrary.arbitrary[String]
      version     ← Arbitrary.arbitrary[String]
      org         ← Arbitrary.arbitrary[String]
      baseurl     ← Arbitrary.arbitrary[String]
      docs        ← Arbitrary.arbitrary[Boolean]
      markdown    ← Arbitrary.arbitrary[String]
      highlighter ← Arbitrary.arbitrary[String]
      defaults    ← listDefaultsArbitrary.arbitrary
      collections ← collectionMapArbitrary.arbitrary
    } yield
      ConfigYaml(name,
                 description,
                 version,
                 org,
                 baseurl,
                 docs,
                 markdown,
                 highlighter,
                 defaults,
                 collections)
  }

  implicit def extraMdConfigArbitrary: Arbitrary[ExtraMdFileConfig] = Arbitrary {
    for {
      file        ← Arbitrary.arbitrary[String]
      target      ← Arbitrary.arbitrary[String]
      mapArbValue ← paletteMapArbitrary.arbitrary
    } yield ExtraMdFileConfig(file, target, mapArbValue)
  }

  implicit def markdownMapArbitrary: Arbitrary[Map[File, ExtraMdFileConfig]] = Arbitrary {
    for {
      n        ← choose(1, 100)
      fileList <- listOfN[File](n, Arbitrary.arbitrary[File])
      config   ← extraMdConfigArbitrary.arbitrary
      map      <- (fileList map (f => f -> config)).toMap
    } yield map
  }

  implicit def settingsArbitrary: Arbitrary[MicrositeSettings] = Arbitrary {
    for {
      name                               ← Arbitrary.arbitrary[String]
      description                        ← Arbitrary.arbitrary[String]
      author                             ← Arbitrary.arbitrary[String]
      homepage                           ← Arbitrary.arbitrary[String]
      twitter                            ← Arbitrary.arbitrary[String]
      highlightTheme                     ← Arbitrary.arbitrary[String]
      micrositeConfigYaml                ← configYamlArbitrary.arbitrary
      micrositeYamlCustom                ← Arbitrary.arbitrary[String]
      micrositeImgDirectory              ← Arbitrary.arbitrary[File]
      micrositeCssDirectory              ← Arbitrary.arbitrary[File]
      micrositeJsDirectory               ← Arbitrary.arbitrary[File]
      micrositeExternalLayoutsDirectory  ← Arbitrary.arbitrary[File]
      micrositeExternalIncludesDirectory ← Arbitrary.arbitrary[File]
      micrositeDataDirectory             ← Arbitrary.arbitrary[File]
      micrositeExtraMdFiles              ← markdownMapArbitrary.arbitrary
      micrositeBaseUrl                   ← Arbitrary.arbitrary[String]
      micrositeDocumentationUrl          ← Arbitrary.arbitrary[String]
      palette                            ← paletteMapArbitrary.arbitrary
      githubOwner                        ← Arbitrary.arbitrary[String]
      githubRepo                         ← Arbitrary.arbitrary[String]
    } yield
      MicrositeSettings(name,
                        description,
                        author,
                        homepage,
                        twitter,
                        highlightTheme,
                        micrositeConfigYaml,
                        micrositeYamlCustom,
                        micrositeImgDirectory,
                        micrositeCssDirectory,
                        micrositeJsDirectory,
                        micrositeExternalLayoutsDirectory,
                        micrositeExternalIncludesDirectory,
                        micrositeDataDirectory,
                        micrositeExtraMdFiles,
                        micrositeBaseUrl,
                        micrositeDocumentationUrl,
                        palette,
                        githubOwner,
                        githubRepo)
  }
}

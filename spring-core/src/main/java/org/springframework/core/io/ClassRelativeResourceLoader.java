/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.core.io;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@link ResourceLoader} implementation that interprets plain resource paths
 * as relative to a given {@code java.lang.Class}.
 *	是 DefaultResourceLoader 的另一个子类的实现。和 FileSystemResourceLoader 类似，
 *	在实现代码的结构上类似，也是覆写 #getResourceByPath(String path) 方法，
 *	并返回其对应的 ClassRelativeContextResource 的资源类型。
 *	ClassRelativeResourceLoader返回的是ClassRelativeContextResource对象，
 * 	ClassRelativeContextResource对象表示上下文相对路径，
 * 	因此猜测ClassRelativeResourceLoader具有从给定的class所在的路径下加载资源的能力：
 * 	假设咱们现在有一个controller叫做LoginController，在com.smart.web包下，
 * 	该包下有一个名为test.xml文件，如果需要加载这个文件咱们就可以这么写：
 *	=============================================================
 *	public String loginPage() throws IOException {
 *		 ResourceLoader resourceLoader=new ClassRelativeResourceLoader(this.getClass());
 *		 Resource resource=resourceLoader.getResource("test.xml");
 *		 System.out.println(resource.getFile().getPath());
 *		 return "index";
 *	}
 *	=============================================================
 * @author Juergen Hoeller
 * @since 3.0
 * @see Class#getResource(String)
 * @see ClassPathResource#ClassPathResource(String, Class)
 */
public class ClassRelativeResourceLoader extends DefaultResourceLoader {

	private final Class<?> clazz;


	/**
	 * Create a new ClassRelativeResourceLoader for the given class.
	 * @param clazz the class to load resources through
	 */
	public ClassRelativeResourceLoader(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		this.clazz = clazz;
		setClassLoader(clazz.getClassLoader());
	}

	/**
	 * ClassRelativeResourceLoader返回的是ClassRelativeContextResource对象，
	 * ClassRelativeContextResource对象表示上下文相对路径，
	 * 因此猜测ClassRelativeResourceLoader具有从给定的class所在的路径下加载资源的能力，
	 * 咱们来个例子验证下：
	 * @param path the path to the resource
	 * @return
	 */
	@Override
	protected Resource getResourceByPath(String path) {
		return new ClassRelativeContextResource(path, this.clazz);
	}


	/**
	 * ClassPathResource that explicitly expresses a context-relative path
	 * through implementing the ContextResource interface.
	 */
	private static class ClassRelativeContextResource extends ClassPathResource implements ContextResource {

		private final Class<?> clazz;

		public ClassRelativeContextResource(String path, Class<?> clazz) {
			super(path, clazz);
			this.clazz = clazz;
		}

		@Override
		public String getPathWithinContext() {
			return getPath();
		}

		@Override
		public Resource createRelative(String relativePath) {
			String pathToUse = StringUtils.applyRelativePath(getPath(), relativePath);
			return new ClassRelativeContextResource(pathToUse, this.clazz);
		}
	}

}

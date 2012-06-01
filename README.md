osemManager
===========

A JPA-like Java Object/Search Engine Mapping (OSEM)
(An Early Example: http://www.compass-project.org/docs/latest/reference/html/core-osem.html)

A an entity store using a search engine document storage. It is a generic factory in front of Elastic Search for now. It is designed to be able to use other search engines, if a driver is written. I paid to have this done, not too sure how well it's documented yet. Probably like most open source, "Read the source code"

One thing is concrete though: As much as possible, the usage pattern will be exactly like JPA. 1/ Get the manager factory, 2/ Get a manager, 3/ Access / Store, etc, entities using the manager.

It is set up to use annotations, I'll elaborate more on this later.

It also has hooks before CRUD actions, like JPA/Hibernate does, so that some amount of ACID can be done if the back end search engine supports versioning.

It will probably be better developed as we integrate it into our project.

TO DOs:
1/ Better docs, like most OSS projects.
2/ Extend usefulness in 'finders' and other search related functionality. For now, it is designed to do feed through of the search engine's API for search.
3/ Describe and post here, how the annotations work.

NOT GOING to DO: (unless I get rich or REALLY see the need to do it)
-----------------------------------------------------------------------
1/ Object management like JPA/Hiberate. In JPA/Hibernate, once the object is in the database and in the session, any changes to the entity automatically get flushed or exception thrown, blah blah (java's not my thing, it's just good to make projects out of. You will have to do ALL your own flushing for any changes to entities once you have them in the search engine/local session.
2/ Enterprise entity management. An even MORE over arching management of entities, as to versioning, detached entities, and other such stuff. Very cool stuff, admirable, NOT going to happen without a lot of help and money. Don't want to reinvent the wheel either. And I don't need it now anyway.

For both of the 'Not going to do its' above, if you want 'em, code 'em and contribute 'em. 

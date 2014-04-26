//
//  Player.m
//  Lord of the Ping
//
//  Created by Matthew Runo on 4/25/14.
//  Copyright (c) 2014 Lord of the Ping. All rights reserved.
//

#import "Player.h"

@implementation Player
@synthesize id;
@synthesize email;
@synthesize password;
@synthesize name;
@synthesize avatarUrl;

-(void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:self.id forKey:@"id"];
    [coder encodeObject:self.email forKey:@"email"];
    [coder encodeObject:self.password forKey:@"password"];
    [coder encodeObject:self.name forKey:@"name"];
    [coder encodeObject:self.avatarUrl forKey:@"avatarUrl"];
}

- (id)initWithCoder:(NSCoder *) coder {
    self = [super init];
    if( self ){
        self.id = [coder decodeObjectForKey:@"id"];
        email = [coder decodeObjectForKey:@"email"];
        password = [coder decodeObjectForKey:@"password"];
        name = [coder decodeObjectForKey:@"name"];
        avatarUrl = [coder decodeObjectForKey:@"avatarUrl"];
    }
    
    return self;
}


@end
